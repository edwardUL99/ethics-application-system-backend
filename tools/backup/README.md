Ethics Application System Back-end Backup Utility
=
This directory contains a Python utility for backing up and restoring data from the database and
also any files uploaded to the server in the specified upload directory. These should be the same
database and upload directory as configured for the server

# Requirements
To use this utility, you need the following installed:
- Python 3.6.3
- Either PostgresSQL or MySQL, depending on the database provider installed

# Run
To run the utility, from the root of the project, you can run the following command to perform a backup:
```
python3 tools/backup/main.py -a backup
```

This creates a backup in the specified backup directory. The backup is in a ZIP format, with two files inside
it:
- dump.sql contains the SQL code to re-import the database and data
- files.zip contains all the files in the upload directory zipped up

To initiate a data restore, you can run the following command:
```
python3 tools/backup/main.py -a restore -b backup_2022_03_31-07_47_31.zip
```
This checks the backup directory for a backup zip of the given name. It re-imports the database and copies all
the files back to the upload directory

The arguments the tool takes are as follows:
```
usage: backup [-h] -a {backup,restore} [-c CONFIG] [-b BACKUP]

A utility for backing up data and uploaded files

optional arguments:
  -h, --help            show this help message and exit
  -a {backup,restore}, --action {backup,restore}
                        The action to carry out, i.e. a backup or restoration of data
  -c CONFIG, --config CONFIG
                        The path to the backup configuration file
  -b BACKUP, --backup BACKUP
                        The backup to restore
```
If `backup` is provided for the `-a` flag, the tool will perform a backup. If `restore` is specified,
you must specify the name of the backup using the `-b` flag. The `-c` flag specifies the path to the
configuration file. It defaults to `backup.yaml` in this directory

# Configuration
The configuration for backups is provided in a YAML file. Ensure that you do not commit
this file to VCS as it contains database user password. The following is a [sample](backup.yaml.sample).
You can take this sample and fill in the properties, and rename the file to `backup.yaml`
```yaml
backup-dir: 'backups'
database:
  provider: 'psql' | 'mysql'
  database: '<database-name>'
  user: '<username>'
  password: '<password>'
files:
  upload-dir: '/tmp/uploads'
```
The properties are as follows:
- *backup-dir*: Can be an absolute path or relative path to the directory where backups will be stored
- *database/provider*: The database provider, either `psql` or `mysql` is supported
- *database/database*: The name of the database to back up
- *database/user*: The name of the database user to back up data with
- *database/password*: The password of the database user
- *files/upload-dir*: The directory where files are uploaded to. This should be the same as configured in the `files` module