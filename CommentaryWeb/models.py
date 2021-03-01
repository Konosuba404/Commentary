from app import db


# Address类对应Address数据库
class Address(db.Model):
    # 声明表名
    __tablename__ = 'Address'
    # 表的结构
    id = db.Column(db.Integer, primary_key=True, autoincrement=True, nullable=False)
    longitude = db.Column(db.Float)
    latitude = db.Column(db.Float)
    address = db.Column(db.Text)
    description = db.Column(db.Text)

    def __init__(self, longitude, latitude, address, description):
        self.longitude = longitude
        self.latitude = latitude
        self.address = address
        self.description = description


# User类对应User数据库
class User(db.Model):
    # 声明表名
    __tablename__ = 'User'
    # 表的结构
    username = db.Column(db.String(20), primary_key=True)
    password = db.Column(db.String(255))

    def __init__(self, username, password):
        self.username = username
        self.password = password
