import yaml
from const import DEFAULT_CONFIG


class ConfigItem:
    """
    A base interface representing a configuration item
    """
    def get(self, prop=None, default=None, require=False):
        """
        Get the property. If a simple property, prop can be ignored, else, it's expected
        :param prop: the name of the property if a nested dictionary
        :param default: a default value to return if the value doesn't exist
        :param require: if True and the property doesn't exist, throw a KeyError
        :return: the property
        """
        raise NotImplementedError('Not Implemented')


class DictConfigItem(ConfigItem):
    """
    A class abstracting a nested YAML dictionary in the config
    """
    def __init__(self, data: dict):
        self._data = data

    def get(self, prop=None, default=None, require=False) -> ConfigItem:
        if not prop:
            return DictConfigItem(self._data)
        else:
            value = self._data.get(prop, default)

            if value:
                if isinstance(value, dict):
                    return DictConfigItem(value)
                else:
                    return value
            else:
                if require:
                    raise KeyError(f'Property {prop} does not exist')
                else:
                    return default


class Configuration(ConfigItem):
    """
    A class to read yaml configuration properties
    """
    def __init__(self, config_file):
        """
        Initialise the configuration with the path to the config file
        :param config_file: the path to the yaml config file
        """
        with open(config_file, 'r') as file:
            self._conf = DictConfigItem(yaml.safe_load(file))

    def get(self, prop=None, default=None, require=False) -> ConfigItem:
        if not prop:
            return self._conf if self._conf else default
        else:
            value = self._conf.get(prop, default)

            if value is None and require:
                raise KeyError(f'Property {prop} does not exist')

            return value


def get_configuration(file: str = DEFAULT_CONFIG) -> ConfigItem:
    """
    Get the configuration identified by the file name
    :param file: the name of the config file to load
    :return: the loaded configuration file
    """
    return Configuration(file)
