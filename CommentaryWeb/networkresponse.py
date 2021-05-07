from flask import Blueprint, request, jsonify
from werkzeug.security import generate_password_hash

from models import Address, User
from point_distance import select_describe
from app import db

bp = Blueprint('network', __name__, url_prefix='/network')


@bp.route('/process', methods=('GET', 'POST'))
def process():
    if request.method == 'POST':
        jsonString = request.get_json()
        # 经度
        longitude = jsonString['Longitude']
        # 纬度
        latitude = jsonString['Latitude']
        direction = jsonString['Direction'].split('：')[0]
        flag = jsonString['Flag']

        # 测试数据
        # dic['lng'] = directions[0]
        # dic['lat'] = directions[1]
        # return jsonify(dic)

        if direction == "北":
            dataAddress = Address.query.filter(Address.latitude > latitude).all()
            distance = select_describe(dataAddress, longitude, latitude, flag)
            return jsonify(distance)

        elif direction == "南":
            dataAddress = Address.query.filter(Address.latitude < latitude).all()
            distance = select_describe(dataAddress, longitude, latitude, flag)
            return jsonify(distance)

        elif direction == "东":
            dataAddress = Address.query.filter(Address.longitude > longitude).all()
            distance = select_describe(dataAddress, longitude, latitude, flag)
            return jsonify(distance)

        elif direction == "西":
            dataAddress = Address.query.filter(Address.longitude < longitude).all()
            distance = select_describe(dataAddress, longitude, latitude, flag)
            return jsonify(distance)

        elif direction == "西北":
            dataAddress = Address.query.filter(Address.latitude > latitude, Address.longitude < longitude).all()
            distance = select_describe(dataAddress, longitude, latitude, flag)
            return jsonify(distance)

        elif direction == "东北":
            dataAddress = Address.query.filter(Address.latitude > latitude, Address.longitude > longitude).all()
            distance = select_describe(dataAddress, longitude, latitude, flag)
            return jsonify(distance)

        elif direction == "西南":
            dataAddress = Address.query.filter(Address.latitude < latitude, Address.longitude < longitude).all()
            distance = select_describe(dataAddress, longitude, latitude, flag)
            return jsonify(distance)

        elif direction == "东南":
            dataAddress = Address.query.filter(Address.latitude < latitude, Address.longitude > longitude).all()
            distance = select_describe(dataAddress, longitude, latitude, flag)
            return jsonify(distance)


# 登录，注册
@bp.route('/user', methods=('GET', 'POST'))
def user():
    if request.method == 'POST':
        data = request.get_json()
        print(data)
        # 用户名
        username = data['username']
        password = data['password']
        print("username" + username)
        print("password" + password)
        # # 创建User对象
        appUser = User(username=username, password=password)
        db.session.add(appUser)
        db.session.commit()
        return jsonify("None")
