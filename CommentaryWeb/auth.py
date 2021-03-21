# 检查用户名字是否存储在session中
# 是的话则从数据库中将对应username的对象取出来
# 否则将g.user设置为空
import functools

from flask import session, g, redirect, url_for

from models import User


@bp.before_app_request
def load_logged_in_user():
    username = session.get('username')
    if username is None:
        g.user = None
    else:
        g.user = User.query.filter_by(username=username).first()


def login_required(view):
    @functools.wraps(view)
    def wrapped_view(**kwargs):
        if g.user is None:
            return redirect(url_for('attest.login'))
        return view(**kwargs)
    return wrapped_view