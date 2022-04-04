import os
import subprocess
from const import list_dbs, POSTGRES, MYSQL


class LoadCommand:
    """
    A base command for loading data into a database
    """

    def load_data(self, username: str, password: str, database: str, source: str):
        """
        Load the data into database
        :param username: the username of the user
        :param password: the password of the user
        :param database: the name of the database to dump
        :param source: the source SQL file
        :return: None
        """
        raise NotImplementedError('not implemented')


class PostgresLoadCommand(LoadCommand):
    def load_data(self, username: str, password: str, database: str, source: str):
        environ = os.environ.copy()
        environ['PGPASSWORD'] = password

        command = ['psql', '-U', username, '-d', database]

        with open(source, 'r') as file:
            process = subprocess.Popen(command, env=environ, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            _, error = process.communicate(file.read().encode())

        if process.returncode != 0 or error:
            print(f'Error occurred: {error.decode()}')
            exit(1)


class MysqlLoadCommand(LoadCommand):
    def load_data(self, username: str, password: str, database: str, source: str):
        command = ['mysql', '-u', username, f'-p{password}', database]
        with open(source, 'r') as f:
            process = subprocess.Popen(command, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            _, error = process.communicate(f.read().encode())

            if process.returncode != 0 or error:
                print(f'Error occurred: {error.decode()}')
                exit(1)


LOAD_COMMANDS = {
    POSTGRES: PostgresLoadCommand(),
    MYSQL: MysqlLoadCommand()
}


def load(provider: str, username: str, password: str, database: str, source: str):
    """
    Dumps the database from the given provider, database and output
    :param provider: the database provider
    :param username: DB username
    :param password: DB password
    :param database: the database name
    :param source: the source file
    :return: None
    """
    providers = list_dbs()

    if provider not in providers:
        print(f'The provider {provider} is not available on the system')
        exit(1)
    else:
        loader: LoadCommand = LOAD_COMMANDS[provider]

        if loader:
            loader.load_data(username, password, database, source)
        else:
            print(f'The tool does not know how to load data into provider {provider}. Implement a DumpCommand class '
                  'for it')
            exit(1)
