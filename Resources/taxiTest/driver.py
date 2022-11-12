import socket
import time
import json

udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
udp_socket.bind(('127.0.0.1', 8081))

data = []
cnt = 0

with open('./testdata.txt') as fin:
    data = fin.readlines()

while True:
    recv_cmd = json.loads(udp_socket.recvfrom(1024)[0])
    if recv_cmd['cmd'] == 'sensor_get' and cnt < len(data):
        sendjson = {}
        sendjson['cmd'] = 'sensor_get'
        sendjson['args'] = recv_cmd['args']
        sendjson['rets'] = " ".join(data[cnt].strip().split(';'))
        cnt = cnt + 1
        udp_socket.sendto(json.dumps(sendjson).encode('utf-8'), ('localhost', 8080))
    elif recv_cmd['cmd'] != 'sensor_get':
        print(recv_cmd)


udp_socket.sendto("hello".encode('utf-8'), ('localhost', 8080))