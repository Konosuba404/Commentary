import math
from math import pi

from flask import Blueprint, request, redirect, url_for, jsonify

from models import Address
from point_distance import select_describe

bp = Blueprint('network', __name__, url_prefix='/network')


@bp.route('/process', methods=('GET', 'POST'))
def process():
    # list1 = []
    if request.method == 'POST':
        jsonString = request.get_json()
        if jsonString['Flag'] == '1':
            # 经度
            longitude = jsonString['Longitude']
            # 纬度
            latitude = jsonString['Latitude']
            direction = jsonString['Direction'].split('：')[0]

            # 测试数据
            # dic['lng'] = directions[0]
            # dic['lat'] = directions[1]
            # return jsonify(dic)

            if direction == "北":
                dataAddress = Address.query.filter(Address.latitude > latitude).all()
                distance = select_describe(dataAddress, longitude, latitude)
                return jsonify(distance)
                # for item in dataAddress:
                #     dic = dict()
                #     dic['lng'] = item.longitude
                #     dic['lat'] = item.latitude
                #     list1.append(dic)
                # redirect(url_for('network.process', jsonString=jsonString, dataFromDatabase=list1))
            elif direction == "南":
                dataAddress = Address.query.filter(Address.latitude < latitude).all()
                distance = select_describe(dataAddress, longitude, latitude)
                return jsonify(distance)
                # for item in dataAddress:
                #     dic = dict()
                #     dic['lng'] = item.longitude
                #     dic['lat'] = item.latitude
                #     list1.append(dic)
                # redirect(url_for('network.process', jsonString=jsonString, dataFromDatabase=list1))
            elif direction == "东":
                dataAddress = Address.query.filter(Address.longitude > longitude).all()
                distance = select_describe(dataAddress, longitude, latitude)
                return jsonify(distance)
                # for item in dataAddress:
                #     dic = dict()
                #     dic['lng'] = item.longitude
                #     dic['lat'] = item.latitude
                #     list1.append(dic)
                # redirect(url_for('network.process', jsonString=jsonString, dataFromDatabase=list1))
            elif direction == "西":
                dataAddress = Address.query.filter(Address.longitude < longitude).all()
                distance = select_describe(dataAddress, longitude, latitude)
                return jsonify(distance)
                # for item in dataAddress:
                #     dic = dict()
                #     dic['lng'] = item.longitude
                #     dic['lat'] = item.latitude
                #     list1.append(dic)
                # redirect(url_for('network.process', jsonString=jsonString, dataFromDatabase=list1))
            elif direction == "西北":
                dataAddress = Address.query.filter(Address.latitude > latitude, Address.longitude < longitude).all()
                distance = select_describe(dataAddress, longitude, latitude)
                return jsonify(distance)
                # for item in dataAddress:
                #     dic = dict()
                #     dic['lng'] = item.longitude
                #     dic['lat'] = item.latitude
                #     list1.append(dic)
                # redirect(url_for('network.process', jsonString=jsonString, dataFromDatabase=list1))
            elif direction == "东北":
                dataAddress = Address.query.filter(Address.latitude > latitude, Address.longitude > longitude).all()
                distance = select_describe(dataAddress, longitude, latitude)
                # dic = dict()
                # dic['lng'] = dataAddress[0].longitude
                # dic['lat'] = dataAddress[0].latitude
                return jsonify(distance)
                # for item in dataAddress:
                #     dic = dict()
                #     dic['lng'] = item.longitude
                #     dic['lat'] = item.latitude
                #     list1.append(dic)
                # redirect(url_for('network.process', jsonString=jsonString, dataFromDatabase=list1))
            elif direction == "西南":
                dataAddress = Address.query.filter(Address.latitude < latitude, Address.longitude < longitude).all()
                distance = select_describe(dataAddress, longitude, latitude)
                return jsonify(distance)
                # for item in dataAddress:
                #     dic = dict()
                #     dic['lng'] = item.longitude
                #     dic['lat'] = item.latitude
                #     list1.append(dic)
                # redirect(url_for('network.process', jsonString=jsonString, dataFromDatabase=list1))
            elif direction == "东南":
                dataAddress = Address.query.filter(Address.latitude < latitude, Address.longitude > longitude).all()
                distance = select_describe(dataAddress, longitude, latitude)
                return jsonify(distance)
                # for item in dataAddress:
                #     dic = dict()
                #     dic['lng'] = item.longitude
                #     dic['lat'] = item.latitude
                #     list1.append(dic)
                # redirect(url_for('network.process', jsonString=jsonString, dataFromDatabase=list1))
        # return jsonify(jsonString)


# # lat lon - > distance
# # 计算经纬度之间的距离，单位为千米
# EARTH_REDIUS = 6378.137
#
#
# def rad(d):
#     return d * pi / 180.0
#
#
# def getDistance(lat1, lng1, lat2, lng2):
#     radLat1 = rad(lat1)
#     radLat2 = rad(lat2)
#     a = radLat1 - radLat2
#     b = rad(lng1) - rad(lng2)
#     s = 2 * math.asin(
#         math.sqrt(math.pow(math.sin(a / 2), 2) + math.cos(radLat1) * math.cos(radLat2) * math.pow(math.sin(b / 2), 2)))
#     s = s * EARTH_REDIUS
#     return s
