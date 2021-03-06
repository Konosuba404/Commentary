import functools
from flask import (
    Blueprint, flash, g, redirect, render_template, session, url_for
)
from werkzeug.security import check_password_hash, generate_password_hash
from models import User
from app import db
from form import RegisterForm, LoginForm

bp = Blueprint('attest', __name__, url_prefix='/attest')


@bp.route('/register', methods=('GET', 'POST'))
def register():
    # 生成表单
    form = RegisterForm()
    # 判断是否是POST方式提交 和 是否提交
    # 等价于 request.method=='post' and form.validate():
    # form.validate()验证数据
    if form.validate_on_submit():
        username = form.username.data
        password = form.password.data
        # 创建User对象
        user = User(username=username, password=generate_password_hash(password))
        db.session.add(user)
        db.session.commit()
        # url_for()根据登录视图的名称生成相应的URL
        # redirect()为生成的URL生成一个重定向响应
        return redirect(url_for('attest.login'))
    # 会渲染一个包含HTML的模板
    return render_template('register.html', form=form)


@bp.route('/login', methods=('GET', 'POST'))
def login():
    form = LoginForm()
    if form.validate_on_submit():
        username = form.username.data
        password = form.password.data
        # 将username作为username查询出密码进行判断
        db_user = User.query.filter_by(username=username).first()
        error = None
        if not check_password_hash(db_user.password, password):
            error = u'密码错误'
        if error is None:
            session.clear()
            session['username'] = form.username.data
            return redirect(url_for('frontend.collect_data'))
        flash(error)
    return render_template('login.html', form=form)


# 注销session重定向到index
@bp.route('/logout')
def logout():
    session.clear()
    return redirect(url_for('attest.login'))
