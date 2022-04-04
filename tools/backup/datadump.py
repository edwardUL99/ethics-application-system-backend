import os
import subprocess
from const import list_dbs, POSTGRES, MYSQL


class DumpCommand:
    """
    A base command for dumping data from a database
    """

    def dump_data(self, username: str, password: str, database: str, destination: str):
        """
        Dump the data from the database
        :param username: the username of the user
        :param password: the password of the user
        :param destination: the destination file
        :param database: the name of the database to dump
        :return: None
        """
        raise NotImplementedError('not implemented')


class PostgresDumpCommand(DumpCommand):
    def dump_data(self, username: str, password: str, database: str, destination: str):
        environ = os.environ.copy()
        environ['PGPASSWORD'] = password

        with open(destination, 'w') as f:
            command = ['pg_dump', '-U', username, '--inserts', '--clean', database]
            process = subprocess.Popen(command, env=environ, stdout=f, stderr=subprocess.PIPE)
            _, error = process.communicate()

            if process.returncode != 0 or error:
                print(f'Error occurred: {error.decode()}')
                exit(1)


class MysqlDumpCommand(DumpCommand):
    def dump_data(self, username: str, password: str, database: str, destination: str):
        command = ['mysqldump', '-u', username, f'-p{password}', '--add-drop-table',
                   '--database', database, '-r', destination]
        process = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        _, error = process.communicate()

        if process.returncode != 0 or error:
            print(f'Error occurred: {error.decode()}')
            exit(1)


DUMP_COMMANDS = {
    POSTGRES: PostgresDumpCommand(),
    MYSQL: MysqlDumpCommand()
}


def dump(provider: str, username: str, password: str, database: str, output: str):
    """
    Dumps the database from the given provider, database and output
    :param provider: the database provider
    :param username: DB username
    :param password: DB password
    :param database: the database name
    :param output: the output file
    :return: None
    """
    providers = list_dbs()

    if provider not in providers:
        print(f'The provider {provider} is not available on the system')
        exit(1)
    else:
        dumper: DumpCommand = DUMP_COMMANDS[provider]

        if dumper:
            dumper.dump_data(username, password, database, output)
        else:
            print(f'The tool does not know how to dump data from provider {provider}. Implement a DumpCommand class '
                  'for it')
            exit(1)
