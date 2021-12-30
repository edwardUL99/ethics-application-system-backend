"""
This script allows testing the back-end API by allowing data to be sent as JSON and pretty-print the JSON response
"""
import argparse
import json
import os.path

import requests

URL = 'http://localhost:8080'

METHOD_CHOICES = ('GET', 'POST', 'PUT', 'DELETE')

parser = argparse.ArgumentParser(description='Sends API requests to the back-end API to visualise the JSON response '
                                             'data')
parser.add_argument('-u', '--url', default=URL, required=False, help='The base URL where the API is hosted at')
parser.add_argument('-f', '--file', default=None, required=False, help='A request JSON file')
parser.add_argument('-e', '--endpoint', default=None, required=False, help='The endpoint to send the request to')
parser.add_argument('-d', '--data', default=None, help='The JSON data to send to the API. Can be a JSON String or a JSON file')
parser.add_argument('-H', '--headers', default=None,
                    help='Headers as a JSON-like String {"Content-Type": "application/json"}')
parser.add_argument('-m', '--method', default='GET', help='The method to send the request with',
                    choices=METHOD_CHOICES)

methods = {
    'GET': requests.get,
    'POST': requests.post,
    'PUT': requests.put,
    'DELETE': requests.delete
}

args = parser.parse_args()


FILE = args.file
ENDPOINT = args.endpoint
DATA = None
HEADERS = None

if FILE and ENDPOINT:
    parser.error('You can only provide a request file using -f or an endpoint using -e. Note that the arguments other'
                 ' than -h or -f are only valid when -e is used')
elif not FILE and not ENDPOINT:
    parser.error('You must specify either -f or -e')
elif FILE:
    if os.path.exists(FILE):
        with open(FILE, 'r') as f:
            request: dict = json.load(f)

        URL = request.get('url', URL)

        if 'endpoint' not in request:
            print('endpoint must be specified in the request file')
            exit(1)
        else:
            ENDPOINT = request.get('endpoint')

        DATA = request.get('data', None)
        HEADERS = request.get('headers', None)
        METHOD = request.get('method', 'GET')

        if METHOD not in METHOD_CHOICES:
            print(f'{METHOD} not a recognized method from {METHOD_CHOICES}')
            exit(1)
    else:
        print(f'The file {FILE} does not exist')
        exit(1)
else:
    DATA = args.data
    HEADERS = args.headers
    METHOD = args.method
    URL = args.url


if not ENDPOINT.startswith('/'):
    ENDPOINT = f'/{ENDPOINT}'

if DATA and isinstance(DATA, str):
    try:
        DATA = json.loads(DATA)
    except ValueError:
        with open(DATA, 'r') as f:
            DATA = json.load(f)

if HEADERS and isinstance(HEADERS, str):
    HEADERS = json.loads(args.headers)


def parse_response(response):
    if response.status_code >= 400:
        print(f'An error occurred: Status Code {response.status_code}')
        print(f'Response Body: {response.content.decode()}')
    else:
        print(f'Response {response.status_code}')
        print(f'JSON:')
        response_json = response.json()
        response_json = json.dumps(response_json, indent=2)
        print(response_json)


def main():
    url = f'{URL}{ENDPOINT}'

    default_headers = {'Content-Type': 'application/json'}
    
    if HEADERS:
        headers = {**default_headers, **HEADERS}
    else:
        headers = default_headers

    method = methods.get(METHOD, methods['GET'])
    response = method(url, json=DATA, headers=headers)

    parse_response(response)


if __name__ == '__main__':
    main()
