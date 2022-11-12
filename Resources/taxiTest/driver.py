import socket
import time
import json

udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
udp_socket.bind(('127.0.0.1', 8081))

data = []

with open('./testdata.txt') as fin:
    data = fin.readlines()

while True:
    recv_cmd = udp_socket.recvfrom(1024)

udp_socket.sendto("hello".encode('utf-8'), ('localhost', 8080))