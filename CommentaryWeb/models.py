from flask_sqlalchemy import SQLAlchemy
from app import create_app

# 先获取app的db对象
db = SQLAlchemy(create_app(), use_native_unicode='utf8')


# Address类对应Address数据库
class Address(db.Model):
    # 声明表名
    __tablename__ = 'Address'
    # 表的结构
    id = db.Column(db.Integer, primary_key=True)
    longitude = db.Column(db.Float)
    latitude = db.Column(db.Float)
    description = db.Column(db.Text)

    def __init__(self, longitude, latitude, description):
        self.longitude = longitude
        self.latitude = latitude
        self.description = description


# User类对应User数据库
class User(db.Model):
    # 声明表名
    __tablename__ = 'User'
    # 表的结构
    username = db.Column(db.String(20), primary_key=True)
    password = db.Column(db.String(10))

    def __init__(self, username, password):
        self.username = username
        self.password = password
