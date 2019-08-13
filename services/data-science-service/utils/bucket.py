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

    def __init__(self, bucket=None, root=None):
        cfg = botocore.client.Config(read_timeout=10000)
        self._s3 = boto3.client(
            "s3",
            aws_access_key_id=os.getenv("aws_access_key_id"),
            aws_secret_access_key=os.getenv("aws_secret_access_key"),
            config=cfg,
        )  # type: boto3.client

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

        # Get the list of files under the specified directory
        self._keys = set(
            [
                k["Key"]
                for k in self._s3.list_objects_v2(
                    Bucket=self._bucket, Prefix=self._root
                )["Contents"]
            ]
        )

        # identify any common prefix
        self._prefix = os.path.commonprefix(list(self._keys))
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
        print("Getting from S3: ", self._prefix + item)
        if not item:
            return None
        response = self._s3.get_object(Bucket=self._bucket, Key=self._prefix + item)
        return response["Body"]
