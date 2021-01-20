from flask_sqlalchemy import SQLAlchemy
from app import create_app

# 先获取app的db对象
db = SQLAlchemy(create_app(), use_native_unicode='utf8')


# Address类对应Address数据库
class Address(db.Model):
    # 声明表名
    __tablename__ = 'Address'
    # 表的结构
    ID = db.Column(db.Integer, primary_key=True)
    Longitude = db.Column(db.Float)
    Latitude = db.Column(db.Float)
    Description = db.Column(db.Text)

    def __init__(self, Longitude, Latitude, Description):
        self.Longitude = Longitude
        self.Latitude = Latitude
        self.Description = Description


# User类对应User数据库
class User(db.Model):
    # 声明表名
    __tablename__ = 'User'
    # 表的结构
    Username = db.Column(db.String(20), primary_key=True)
    Password = db.Column(db.String(10))

    def __init__(self, Username, Password):
        self.Username = Username
        self.Password = Password