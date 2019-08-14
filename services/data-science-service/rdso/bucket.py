"""
Data access utilities
"""
from collections.abc import Mapping
import os
import boto3
import botocore.client


class Bucket(Mapping):
    """
    Convenience interface to files in S3 bucket
    Is a Mapping from 'name' to file stream
    """

    def __init__(self, bucket=None, root=None, quiet=False, **kwargs):
        cfg = botocore.client.Config(read_timeout=10000)
        for k in kwargs:
            os.environ[k] = kwargs[k]
        try:
            profile = os.environ['AWS_PROFILE']
            session = boto3.Session(profile_name=profile)
            self._s3 = session.client('s3', config=cfg)
        except KeyError:
            try:
                self._s3 = boto3.client(
                    "s3",
                    aws_access_key_id=os.environ["aws_access_key_id"],
                    aws_secret_access_key=os.environ["aws_secret_access_key"],
                    config=cfg,
                )  # type: boto3.client
            except KeyError:
                raise ValueError("No AWS credentials found")

        self.quiet = quiet

        if not bucket:
            self._bucket = os.getenv("stel_s3_bucket")
        else:
            self._bucket = str(bucket)

        if not self._bucket:
            raise ValueError("No S3 bucket has been specified.")

        if not root:
            self._root = ""
        else:
            self._root = str(root)

        # Helper function for s3 pagination
        def get_all_s3_objects(s3, **base_kwargs):
            continuation_token = None
            while True:
                list_kwargs = dict(MaxKeys=1000, **base_kwargs)
                if continuation_token:
                    list_kwargs['ContinuationToken'] = continuation_token
                response = s3.list_objects_v2(**list_kwargs)
                yield from response.get('Contents', [])
                if not response.get('IsTruncated'):  # At the end of the list?
                    break
                continuation_token = response.get('NextContinuationToken')

        # Get the list of files under the specified directory
        self._keys = set(
            [
                k["Key"]
                for k in get_all_s3_objects(self._s3,
                                            Bucket=self._bucket, Prefix=self._root
                                            )
            ]
        )

        # identify any common prefix
        self._prefix = None
        # self._prefix = os.path.commonprefix(list(self._keys))
        # self._keys = set([k.replace(self._prefix, "") for k in self._keys])

    def __len__(self) -> int:
        return len(self._keys)

    def __contains__(self, item: str) -> bool:
        """
        Does the file exist in the bucket?
        Parameters
        ----------
        item
        Returns
        -------
        :
            True if connection works and file exists, False otherwise
        """
        return item in self._keys

    def __iter__(self):
        yield from (x for x in self._keys if x != self._root + "/")

    def __getitem__(self, item):
        """
        Get a streaming response with contents of file
        Parameters
        ----------
        item
        Returns
        -------
        """
        # return file stream...
        if not item:
            return None
        if self._prefix:
            if not self.quiet:
                print("Getting from S3: ", self._prefix + item)
            response = self._s3.get_object(Bucket=self._bucket, Key=self._prefix + item)
        else:
            if not self.quiet:
                print("Getting from S3: ", item)
            response = self._s3.get_object(
                Bucket=self._bucket, Key=self._root + "/" + item
            )

        return response["Body"]
