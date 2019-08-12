import os
import boto3
import yaml
import subprocess
from yaml.representer import SafeRepresenter


class literal_str(str): pass


def change_style(style, representer):
    def new_representer(dumper, data):
        scalar = representer(dumper, data)
        scalar.style = style
        return scalar
    return new_representer


# represent_str does handle some corner cases, so use that
# instead of calling represent_scalar directly
represent_literal_str = change_style('|', SafeRepresenter.represent_str)


def representer_quote(dumper, data):
    return dumper.represent_scalar('tag:yaml.org,2002:str', data, style=' "')


yaml.add_representer(str, representer_quote)


class Configuration:
    CONFIG = None

    def __init__(self):
        self.access_key = os.environ['AWS_ACCESS_KEY_ID']
        self.secret_key = os.environ['AWS_SECRET_ACCESS_KEY']
        self.gh_u = os.environ['GITHUB_USERNAME']
        self.gh_p = os.environ['GITHUB_PASSWORD']

        self.account_id = boto3.client('sts',
                                       aws_access_key_id=self.access_key,
                                       aws_secret_access_key=self.secret_key
                                       ).get_caller_identity().get('Account')
        os.environ['AWS_ACCOUNT_ID'] = self.account_id
        self.CONFIG = self.config_from_file
        self.set_config('aws_account_id', self.account_id)
        self.set_config('rds_password', self.gh_p)
        self.set_config('jupyter_password', self.gh_p)
        self.set_config('tf_backend', 'mdas-state-demo-' + self.account_id)
        self.set_config('aws_access_key_id', self.access_key)
        self.set_config('aws_secret_access_key', self.secret_key)

    @staticmethod
    def get_ssh():
        with open('/root/.ssh/id_rsa', 'r') as rsa:
            key = rsa.read()
            return literal_str(key)

    @property
    def config_from_file(self):
        with open('../template.credentials.yaml', "r") as config_file:
            return yaml.safe_load(config_file)

    def set_config(self, key, value):
        if key == 'git_private_key':
            self.CONFIG[key] = literal_str(value)
        else:
            self.CONFIG[key] = value

    def write(self):
        with open('../scripts/credentials.yaml', "w") as config_file:
            print(self.CONFIG)
            yaml.add_representer(literal_str, represent_literal_str)
            yaml.dump(self.CONFIG, config_file, default_flow_style=False, default_style=None)


if __name__ == '__main__':
    c = Configuration()
    subprocess.run(['bash tools/set-gh-key.sh'], shell=True, check=True)
    c.set_config('git_private_key', c.get_ssh())
    c.write()
