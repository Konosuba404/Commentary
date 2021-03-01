from flask import Blueprint, redirect, url_for, render_template, request

from app import nav, db
from flask_nav.elements import Navbar, View, Link

from models import Address

bp = Blueprint('frontend', __name__, url_prefix='/frontend')

# 为导航栏注册组件
nav.register_element('top', Navbar(
    View(u'地图', 'frontend.collect_data'),
    Link(u'数据表', 'data_table'),
    Link(u'退出登录', 'logout_from_this')
))


# 显示地图
@bp.route('/collect_data', methods=('GET', 'POST'))
def collect_data():
    return render_template('collect_data.html')


# 处理插入数据路由
@bp.route('/insert_data', methods=('GET', 'POST'))
def insert_data():
    if request.method == 'POST':
        data = request.get_json()
        print(data)
        print(type(data))
        for i in data:
            lng = i['longitude']
            lat = i['latitude']
            addr = i['address']
            description = i['description']
            address = Address(longitude=lng, latitude=lat, address=addr, description=description)
            db.session.add(address)
        db.session.commit()
        print("插入成功")
        return redirect(url_for('frontend.data_table'))


# 显示数据表
@bp.route('/data_table', methods=('GET', 'POST'))
def data_table():
    data = Address.query.all()
    return render_template('data_table.html', data=data)


# 处理删除数据路由
@bp.route('/delete_data', methods=('GET', 'POST'))
def delete_data():
    if request.method == 'POST':
        data = request.get_json()
        print(data)
        lng = float(data['longitude'])
        lat = float(data['latitude'])
        # print(lng+"->"+lat)
        print(type(lng))
        # filter_by:直接用属性名，比较用=；大小于不支持；and_和or_不支持
        # filter   :用类名.属性名，比较用==；大小于支持；and_和or_支持
        # address = Address.query.filter(longitude==lng, latitude==lat).first()
        address = Address.query.filter_by(longitude=lng, latitude=lat).first()
        db.session.delete(address)
        db.session.commit()
    return redirect(url_for('frontend.data_table'))


# 退出登录
@bp.route('/logout_from_this')
def logout_from_this():
    return redirect(url_for('attest.logout'))
