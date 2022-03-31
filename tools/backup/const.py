import os
import subprocess

BASE_DIR = os.path.abspath(os.path.dirname(__file__))
DEFAULT_CONFIG = os.path.join(BASE_DIR, 'backup.yaml')

POSTGRES = 'psql'
MYSQL = 'mysql'
SUPPORTED_DBS = [POSTGRES, MYSQL]
FILE_TIMESTAMP_FORMAT = '%Y_%m_%d-%H_%M_%S'


def _check_db_installed(db: str) -> bool:
    """
    Check if the database is installed
    :param db: the database to check
    :return: the
    """
    process = subprocess.Popen(['which', db], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout, _ = process.communicate()

    return stdout and process.returncode == 0


def list_dbs():
    """
    List the installed and available databases
    :return: list of installed and available databases
    """
    return [db for db in SUPPORTED_DBS if _check_db_installed(db)]

