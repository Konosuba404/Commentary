from flask import Blueprint, request, redirect, url_for, jsonify

from models import Address

bp = Blueprint('network', __name__, url_prefix='/network')


@bp.route('/process', methods=('GET', 'POST'))
def process():
    if request.method == 'POST':
        jsonString = request.get_json()
        # 经度
        longitude = jsonString['Longitude']
        # 纬度
        latitude = jsonString['Latitude']
        direction = jsonString['Direction'].split(':')[0]
        degree = jsonString['Direction'].split(':')[1]
        if direction == '北':
            address = Address.query.filter(Address.latitude > latitude).all()
        elif direction == '南':
            address = Address.query.filter(Address.latitude < latitude).all()
        elif direction == '东':
            address = Address.query.filter(Address)
        return redirect(url_for('network.process', jsonString=jsonString, address=address))
    return jsonify(None)
