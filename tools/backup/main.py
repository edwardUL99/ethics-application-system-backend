import os
import tempfile
import argparse
import time
import zipfile

from datadump import dump
from dataload import load
from files import backup, restore
from config import get_configuration
from const import DEFAULT_CONFIG, list_dbs, FILE_TIMESTAMP_FORMAT

BACKUP = 'backup'
RESTORE = 'restore'

dbs = list_dbs()


def _parse_DB_Config(config):
    database_config = config.get('database', require=True)
    provider = database_config.get('provider', require=True)

    if provider not in dbs:
        raise RuntimeError(f'Provider {provider} not available')

    database = database_config.get('database', require=True)
    user = database_config.get('user', require=True)
    password = database_config.get('password', require=True)

    return provider, database, user, password


def _backup_DB(config, destination):
    provider, database, user, password = _parse_DB_Config(config)
    destination = os.path.join(destination, 'dump.sql')
    dump(provider, user, password, database, destination)

    return destination


def _backup_files(config, destination):
    files_config = config.get('files', require=True)
    upload_dir = files_config.get('upload-dir', require=True)

    return backup(upload_dir, os.path.join(destination, 'files.zip'))


def do_backup(config, destination):
    sql_file = _backup_DB(config, destination)
    files_zip = _backup_files(config, destination)
    backup_dir = config.get('backup-dir', require=True)

    if not os.path.isdir(backup_dir):
        os.makedirs(backup_dir)

    backup_dir = os.path.abspath(os.path.join(backup_dir, f'backup_{time.strftime(FILE_TIMESTAMP_FORMAT)}.zip'))

    os.chdir(os.path.dirname(sql_file))
    sql_file = os.path.basename(sql_file)
    files_zip = os.path.basename(files_zip)

    with zipfile.ZipFile(backup_dir, 'w') as zipped:
        zipped.write(sql_file)
        zipped.write(files_zip)


def _restore_DB(config, sql_file):
    provider, database, user, password = _parse_DB_Config(config)
    load(provider, user, password, database, sql_file)


def _restore_files(config, files_zip):
    files_config = config.get('files', require=True)
    upload_dir = files_config.get('upload-dir', require=True)

    restore(upload_dir, files_zip)


def do_restore(config, backup_file: str):
    backup_dir = config.get('backup-dir', require=True)
    backup_file = os.path.abspath(os.path.join(backup_dir, backup_file))

    if os.path.isfile(backup_file):
        saved = os.getcwd()

        with tempfile.TemporaryDirectory() as tmp_dir:
            os.chdir(tmp_dir)

            with zipfile.ZipFile(backup_file, 'r') as zipped:
                zipped.extractall()

            sql_file = os.path.join(tmp_dir, 'dump.sql')
            zip_file = os.path.join(tmp_dir, 'files.zip')

            _restore_DB(config, sql_file)
            _restore_files(config, zip_file)

        os.chdir(saved)
    else:
        raise RuntimeError(f'Backup {backup_file} does not exist')


def _parse_args():
    parser = argparse.ArgumentParser('backup', description='A utility for backing up data and uploaded files')
    parser.add_argument('-a', '--action', choices=(BACKUP, RESTORE), required=True,
                        help='The action to carry out, i.e. a backup or restoration of data')
    parser.add_argument('-c', '--config', default=DEFAULT_CONFIG, help='The path to the backup configuration file')
    parser.add_argument('-b', '--backup', required=False, help='The backup to restore')

    return parser.parse_args(), parser


def _action(configuration, args, parser):
    with tempfile.TemporaryDirectory() as tempDir:
        destination = os.path.join(tempDir, time.strftime(FILE_TIMESTAMP_FORMAT))
        os.makedirs(destination)

        if args.action == BACKUP:
            do_backup(configuration, destination)
        elif args.action == RESTORE:
            if not args.backup:
                raise RuntimeError('If restore is specified, you must choose the backup to restore')

            do_restore(configuration, args.backup)
        else:
            parser.print_help()


def main():
    args, parser = _parse_args()
    config = get_configuration(args.config)
    _action(config, args, parser)


if __name__ == '__main__':
    main()
