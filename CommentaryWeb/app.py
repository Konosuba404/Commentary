import os

from flask import Flask
from flask_bootstrap import Bootstrap
from flask_nav import Nav
from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()
nav = Nav()


def create_app():
    # instance_relative_config告诉配置文件是相对于instance folder文件夹的
    app = Flask(__name__, instance_relative_config=True)
    # 加入数据库配置
    app.config.from_object('dbconfig')
    app.config['SECRET_KEY'] = os.urandom(24)
    app.config['RECAPTCHA_PUBLIC_KEY'] = 'public'
    # 将上下文对象推入栈中
    app.app_context().push()
    # 初始化db
    db.init_app(app)
    # 初始化Bootstrap
    Bootstrap(app)
    # 初始化Nav
    nav.init_app(app)
    # 注册蓝图
    import attest
    # import background
    import frontend
    import networkresponse
    app.register_blueprint(attest.bp)
    # app.register_blueprint(background.bp)
    app.register_blueprint(frontend.bp)
    app.register_blueprint(networkresponse.bp)
    return app


if __name__ == '__main__':
    create_app().run()
