from flask import Blueprint, request

bp = Blueprint('network', __name__, url_prefix='/network')


@bp.route('/process', methods=('GET', 'POST'))
def process():
    jsonString = request.get_json()
    print(jsonString)
