"""
This script allows testing the back-end API by allowing data to be sent as JSON and pretty-print the JSON response
"""
import argparse
import json

import requests

URL = 'http://localhost:8080'

parser = argparse.ArgumentParser(description='Sends API requests to the back-end API to visualise the JSON response '
                                             'data')
parser.add_argument('-u', '--url', default=URL, required=False, help='The base URL where the API is hosted at')
parser.add_argument('-e', '--endpoint', required=True, help='The endpoint to send the request to')
parser.add_argument('-d', '--data', default=None, help='The JSON data to send to the API. Can be a JSON String or a JSON file')
parser.add_argument('-H', '--headers', default=None,
                    help='Headers as a JSON-like String {"Content-Type": "application/json"}')
parser.add_argument('-m', '--method', default='GET', help='The method to send the request with',
                    choices=('GET', 'POST', 'PUT', 'DELETE'))

methods = {
    'GET': requests.get,
    'POST': requests.post,
    'PUT': requests.put,
    'DELETE': requests.delete
}

args = parser.parse_args()

URL = args.url
ENDPOINT = args.endpoint

if not ENDPOINT.startswith('/'):
    ENDPOINT = f'/{ENDPOINT}'
    
if not ENDPOINT.startswith('/api'):
    ENDPOINT = f'/api{ENDPOINT}'

DATA = args.data

if DATA:
    try:
        DATA = json.loads(DATA)
    except ValueError:
        with open(DATA, 'r') as f:
            DATA = json.load(f)

HEADERS = None if not args.headers else json.loads(args.headers)


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

    method = methods.get(args.method, methods['GET'])
    response = method(url, json=DATA, headers=headers)

    parse_response(response)


if __name__ == '__main__':
    main()
