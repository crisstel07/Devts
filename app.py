from flask import Flask, request, jsonify
from flask_mysqldb import MySQL
from flask_bcrypt import Bcrypt
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
import random
import string
import re  # Para validación de correo
from datetime import datetime, timedelta  #  Importar para manejar fechas y expiración
import secrets  #  Importar para generar códigos criptográficamente seguros

app = Flask(__name__)
bcrypt = Bcrypt(app)

# --- Configuración de la Base de Datos ---
app.config['MYSQL_HOST'] = 'localhost'
app.config['MYSQL_USER'] = 'root'
app.config['MYSQL_PASSWORD'] = '123456789'
app.config['MYSQL_DB'] = 'VEILWALKER'

mysql = MySQL(app)

# --- Configuración de Correo Electrónico ---
EMAIL_ADDRESS = 'gamerveilwalker@gmail.com'
EMAIL_PASSWORD = 'rjsr cgju cbzn dgpr'


# --- Funciones Auxiliares ---
def generate_verify_code():
    """Genera un código de verificación aleatorio de 6 dígitos (para registro)."""
    return ''.join(random.choices(string.digits, k=6))


def generate_reset_code():
    """¡NUEVO! Genera un código de restablecimiento aleatorio de 6 dígitos (para contraseña)."""
    # Usamos secrets para una generación más segura que random para códigos sensibles
    return ''.join(secrets.choice(string.digits) for i in range(6))

def send_email(to_email, subject, body):
    """Envía un correo electrónico a la dirección especificada."""
    try:
        msg = MIMEMultipart()
        msg['From'] = EMAIL_ADDRESS
        msg['To'] = to_email
        msg['Subject'] = subject
        msg.attach(MIMEText(body, 'plain'))

        with smtplib.SMTP_SSL('smtp.gmail.com', 465) as smtp:
            smtp.login(EMAIL_ADDRESS, EMAIL_PASSWORD)
            smtp.send_message(msg)
        print(f"Correo enviado a {to_email} con asunto: {subject}")
        return True
    except Exception as e:
        print(f"Error sending email to {to_email}: {e}")
        return False


def is_valid_email(email):
    """Valida si la cadena de texto es un formato de correo electrónico válido."""
    return re.match(r"[^@]+@[^@]+\.[^@]+", email)


# --- Rutas de la API ---

@app.route('/api/register', methods=['POST'])
def register_user():
    """Endpoint para el registro de nuevos usuarios."""
    data = request.get_json()

    nombre_usuario = data.get('nombre_usuario')
    correo = data.get('correo')
    password = data.get('password')

    if not all([nombre_usuario, correo, password]):
        return jsonify(
            {'success': False, 'message': 'Faltan datos requeridos (nombre_usuario, correo, password).'}), 400

    if not is_valid_email(correo):
        return jsonify({'success': False, 'message': 'Formato de correo electrónico inválido.'}), 400

    hashed_password = bcrypt.generate_password_hash(password).decode('utf-8')
    verify_code = generate_verify_code()

    try:
        cur = mysql.connection.cursor()

        cur.execute("SELECT id FROM Usuarios WHERE nombre_usuario = %s OR correo = %s", (nombre_usuario, correo))
        existing_user = cur.fetchone()
        if existing_user:
            cur.close()
            return jsonify({'success': False,
                            'message': 'El nombre de usuario o el correo ya existen.'}), 409

        cur.execute(
            "INSERT INTO Usuarios (nombre_usuario, correo, password, VerifyCode, Status) VALUES (%s, %s, %s, %s, %s)",
            (nombre_usuario, correo, hashed_password, verify_code, 'Pending'))
        mysql.connection.commit()

        user_id = cur.lastrowid
        cur.close()

        email_subject = "Tu Código de Verificación para VEILWALKER"
        email_body = f"Hola {nombre_usuario},\n\nGracias por registrarte en VEILWALKER. Tu código de verificación es: {verify_code}\n\nPor favor, introduce este código en la aplicación para activar tu cuenta.\n\nSaludos,\nEl equipo de VEILWALKER."
        email_sent = send_email(correo, email_subject, email_body)

        if email_sent:
            return jsonify(
                {'success': True, 'message': 'Registro exitoso. Revisa tu correo para el código de verificación.',
                 'data': {'user_id': user_id, 'username': nombre_usuario, 'email': correo}}), 201
        else:
            return jsonify({'success': False,
                            'message': 'Registro exitoso, pero falló el envío del correo de verificación. Contacta a soporte.',
                            'data': {'user_id': user_id, 'username': nombre_usuario, 'email': correo}}), 200

    except Exception as e:
        print(f"Error en el registro: {e}")
        return jsonify({'success': False, 'message': 'Error interno del servidor durante el registro.'}), 500


@app.route('/api/verify', methods=['POST'])
def verify_user():
    """Endpoint para verificar el código enviado al correo."""
    data = request.get_json()

    print(f"Flask: JSON recibido en /verify: {data}")

    if not data:
        return jsonify({'success': False, 'message': 'No se proporcionaron datos JSON.'}), 400

    correo = data.get('correo')
    code = data.get('code')

    print(f"Flask: Extraído correo: {correo}, code: {code}")

    if not all([correo, code]):
        return jsonify({'success': False, 'message': 'Faltan datos (correo, code).'}), 400

    try:
        cur = mysql.connection.cursor()
        cur.execute("SELECT id, Status FROM Usuarios WHERE correo = %s AND VerifyCode = %s", (correo, code))
        user_found = cur.fetchone()

        if user_found:
            db_user_id, db_status = user_found

            if db_status == 'Verified':
                cur.close()
                return jsonify(
                    {'success': False, 'message': 'Tu cuenta ya ha sido verificada.'}), 400

            cur.execute("UPDATE Usuarios SET Status = 'Verified', VerifyCode = NULL WHERE id = %s", (db_user_id,))
            mysql.connection.commit()
            cur.close()
            return jsonify({'success': True, 'message': '¡Cuenta verificada exitosamente!'}), 200
        else:
            cur.close()
            return jsonify(
                {'success': False, 'message': 'Correo o código de verificación inválido.'}), 400

    except Exception as e:
        print(f"Error en la verificación: {e}")
        return jsonify({'success': False, 'message': 'Error interno del servidor durante la verificación.'}), 500


@app.route('/api/login', methods=['POST'])
def login_user():
    """Endpoint para el inicio de sesión de usuarios."""
    data = request.get_json()
    correo = data.get('correo')
    password = data.get('password')

    if not all([correo, password]):
        return jsonify({'success': False, 'message': 'Faltan datos (correo, password).'}), 400

    try:
        cur = mysql.connection.cursor()
        cur.execute(
            "SELECT id, nombre_usuario, correo, password, Status, foto_perfil_url FROM Usuarios WHERE correo = %s",
            (correo,))
        user_data = cur.fetchone()

        if user_data:
            db_id, db_nombre_usuario, db_correo, db_hashed_password, db_status, db_foto_perfil_url = user_data

            if bcrypt.check_password_hash(db_hashed_password, password):
                if db_status == 'Verified':
                    return jsonify({
                        'success': True,
                        'message': '¡Inicio de sesión exitoso!',
                        'data': {
                            'id': db_id,
                            'nombre_usuario': db_nombre_usuario,
                            'correo': db_correo,
                            'foto_perfil_url': db_foto_perfil_url
                        }
                    }), 200
                else:
                    return jsonify({'success': False,
                                    'message': 'Cuenta no verificada. Por favor, verifica tu correo.'}), 401
            else:
                return jsonify({'success': False,
                                'message': 'Credenciales inválidas (correo o contraseña incorrectos).'}), 401
        else:
            return jsonify({'success': False,
                            'message': 'Credenciales inválidas (correo o contraseña incorrectos).'}), 401

    except Exception as e:
        print(f"Error en el inicio de sesión: {e}")
        return jsonify({'success': False, 'message': 'Error interno del servidor durante el inicio de sesión.'}), 500


# NUEVOS ENDPOINTS PARA EL PERFIL E INVENTARIO
@app.route('/api/profile/<int:user_id>', methods=['GET'])
def get_or_create_character_profile(user_id):
    """
    Endpoint para obtener el perfil de un personaje.
    Si el personaje no existe para el user_id dado, crea uno nuevo con valores por defecto.
    Alineado con el esquema de tu tabla Personajes.
    """
    try:
        cur = mysql.connection.cursor()

        cur.execute(
            "SELECT id, user_id, nombre_personaje, vida_actual, nivel, experiencia, energia, tiempo_juego, last_save_point FROM Personajes WHERE user_id = %s",
            (user_id,))
        character_tuple = cur.fetchone()

        if character_tuple is None:
            cur.execute(
                "INSERT INTO Personajes (user_id, nombre_personaje, vida_actual, nivel, experiencia, energia, tiempo_juego, last_save_point) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)",
                (user_id, None, 100, 1, 0, 4, '00:00:00', None))
            mysql.connection.commit()
            new_character_id = cur.lastrowid

            cur.execute(
                "SELECT id, user_id, nombre_personaje, vida_actual, nivel, experiencia, energia, tiempo_juego, last_save_point FROM Personajes WHERE id = %s",
                (new_character_id,))
            character_tuple = cur.fetchone()

            if character_tuple:
                character = {
                    'id': character_tuple[0],
                    'user_id': character_tuple[1],
                    'nombre_personaje': character_tuple[2],
                    'vida_actual': character_tuple[3],
                    'nivel': character_tuple[4],
                    'experiencia': character_tuple[5],
                    'energia': character_tuple[6],
                    'tiempo_juego': str(character_tuple[7]),
                    'last_save_point': character_tuple[8]
                }
                return jsonify({'success': True, 'message': 'Nuevo personaje creado.', 'data': character}), 201
            else:
                return jsonify({'success': False, 'message': 'Error al recuperar el personaje recién creado.'}), 500
        else:
            character = {
                'id': character_tuple[0],
                'user_id': character_tuple[1],
                'nombre_personaje': character_tuple[2],
                'vida_actual': character_tuple[3],
                'nivel': character_tuple[4],
                'experiencia': character_tuple[5],
                'energia': character_tuple[6],
                'tiempo_juego': str(character_tuple[7]),
                'last_save_point': character_tuple[8]
            }
            return jsonify({'success': True, 'message': 'Perfil de personaje cargado.', 'data': character}), 200

    except Exception as e:
        print(f"Error en get_or_create_character_profile: {e}")
        return jsonify(
            {'success': False, 'message': 'Error interno del servidor al cargar/crear personaje: ' + str(e)}), 500


@app.route('/api/profile/<int:character_id>', methods=['PUT'])
def update_character_profile(character_id):
    """
    Endpoint para actualizar el perfil de un personaje.
    Alineado con el esquema de tu tabla Personajes.
    """
    data = request.get_json()
    if not data:
        return jsonify({'success': False, 'message': 'No se proporcionaron datos JSON para actualizar.'}), 400

    update_fields = []
    update_values = []

    if 'nombre_personaje' in data:
        update_fields.append('nombre_personaje = %s')
        update_values.append(data['nombre_personaje'])
    if 'vida_actual' in data:
        update_fields.append('vida_actual = %s')
        update_values.append(data['vida_actual'])
    if 'nivel' in data:
        update_fields.append('nivel = %s')
        update_values.append(data['nivel'])
    if 'experiencia' in data:
        update_fields.append('experiencia = %s')
        update_values.append(data['experiencia'])
    if 'energia' in data:
        update_fields.append('energia = %s')
        update_values.append(data['energia'])
    if 'tiempo_juego' in data:
        update_fields.append('tiempo_juego = %s')
        update_values.append(data['tiempo_juego'])
    if 'last_save_point' in data:
        update_fields.append('last_save_point = %s')
        update_values.append(data['last_save_point'])

    if not update_fields:
        return jsonify({'success': False, 'message': 'No se proporcionaron campos válidos para actualizar.'}), 400

    try:
        cur = mysql.connection.cursor()
        query = "UPDATE Personajes SET " + ", ".join(update_fields) + " WHERE id = %s"
        update_values.append(character_id)

        cur.execute(query, tuple(update_values))
        mysql.connection.commit()
        cur.close()

        if cur.rowcount == 0:
            return jsonify({'success': False, 'message': 'Personaje no encontrado o no se realizaron cambios.'}), 404
        return jsonify({'success': True, 'message': 'Perfil de personaje actualizado exitosamente.'}), 200

    except Exception as e:
        print(f"Error en update_character_profile: {e}")
        return jsonify({'success': False, 'message': 'Error interno del servidor al actualizar personaje.'}), 500


@app.route('/api/users/<int:user_id>/profile_picture', methods=['PUT'])
def update_user_profile_picture(user_id):
    """
    Endpoint para actualizar la foto de perfil del usuario.
    Alineado con el esquema de tu tabla Usuarios.
    """
    data = request.get_json()
    new_photo_url = data.get('foto_perfil_url')

    if not new_photo_url:
        return jsonify({"success": False, "message": "URL de foto de perfil no proporcionada."}), 400

    try:
        cur = mysql.connection.cursor()
        cur.execute("UPDATE Usuarios SET foto_perfil_url = %s WHERE id = %s", (new_photo_url, user_id))
        mysql.connection.commit()
        cur.close()
        if cur.rowcount == 0:
            return jsonify({"success": False, "message": "Usuario no encontrado o no se realizó cambio."}), 404
        return jsonify({"success": True, "message": "Foto de perfil del usuario actualizada."}), 200
    except Exception as e:
        print(f"Error al actualizar foto de perfil del usuario: {e}")
        return jsonify(
            {"success": False, "message": f"Error interno del servidor al actualizar foto de perfil: {str(e)}"}), 500


@app.route('/api/inventory/<int:character_id>', methods=['GET'])
def get_character_inventory(character_id):
    """
    Endpoint para obtener el inventario de un personaje.
    Une con la tabla Items para proporcionar detalles completos de cada ítem en el inventario.
    Alineado con el esquema de tus tablas Inventario e Items.
    """
    try:
        cur = mysql.connection.cursor()
        cur.execute("""
            SELECT
                inv.slot,
                inv.cantidad,
                it.id AS item_id,
                it.nombre,
                it.descripcion,
                it.tipo,
                it.efecto,
                it.valor_efecto,
                it.icono_url,
                it.apilable
            FROM Inventario inv
            JOIN Items it ON inv.item_id = it.id
            WHERE inv.personaje_id = %s
            ORDER BY inv.slot ASC
        """, (character_id,))
        inventory_items_data = cur.fetchall()
        cur.close()

        inventory_list = []
        for item_data in inventory_items_data:
            item_entry = {
                'slot': item_data[0],
                'cantidad': item_data[1],
                'item_details': {
                    'id': item_data[2],
                    'nombre': item_data[3],
                    'descripcion': item_data[4],
                    'tipo': item_data[5],
                    'efecto': item_data[6],
                    'valor_efecto': item_data[7],
                    'icono_url': item_data[8],
                    'apilable': bool(item_data[9])
                }
            }
            inventory_list.append(item_entry)

        return jsonify({'success': True, 'message': 'Inventario cargado.', 'data': inventory_list}), 200

    except Exception as e:
        print(f"Error en get_character_inventory: {e}")
        return jsonify({'success': False, 'message': 'Error interno del servidor al cargar inventario.'}), 500


@app.route('/api/inventory/<int:character_id>/add', methods=['POST'])
def add_item_to_inventory(character_id):
    """
    Endpoint para añadir o actualizar un ítem en el inventario.
    Maneja ítems apilables y no apilables, y la gestión de slots.
    Alineado con el esquema de tus tablas Inventario e Items.
    """
    data = request.get_json()
    item_id = data.get('item_id')
    cantidad_a_anadir = data.get('cantidad', 1)
    slot_sugerido = data.get('slot')

    if not item_id:
        return jsonify({'success': False, 'message': 'Falta el item_id.'}), 400

    try:
        cur = mysql.connection.cursor()

        cur.execute("SELECT nombre, apilable FROM Items WHERE id = %s", (item_id,))
        item_details = cur.fetchone()
        if not item_details:
            cur.close()
            return jsonify({'success': False, 'message': 'Item no encontrado.'}), 404

        item_nombre, item_apilable = item_details

        if item_apilable:
            cur.execute("SELECT id, cantidad, slot FROM Inventario WHERE personaje_id = %s AND item_id = %s",
                        (character_id, item_id))
            existing_inventory_item = cur.fetchone()

            if existing_inventory_item:
                inv_id, current_cantidad, slot_ocupado = existing_inventory_item
                new_cantidad = current_cantidad + cantidad_a_anadir
                cur.execute("UPDATE Inventario SET cantidad = %s WHERE id = %s", (new_cantidad, inv_id))
                mysql.connection.commit()
                cur.close()
                return jsonify({'success': True,
                                'message': f'{cantidad_a_anadir} {item_nombre}(s) añadidos al slot {slot_ocupado}.',
                                'data': {'slot': slot_ocupado}}), 200
            else:
                slot_to_use = slot_sugerido
                if slot_to_use:
                    cur.execute("SELECT id FROM Inventario WHERE personaje_id = %s AND slot = %s",
                                (character_id, slot_to_use))
                    if cur.fetchone():
                        cur.close()
                        return jsonify({'success': False,
                                        'message': f'El slot {slot_to_use} ya está ocupado. No se puede añadir {item_nombre}.'}), 409
                else:
                    occupied_slots = set()
                    cur.execute("SELECT slot FROM Inventario WHERE personaje_id = %s", (character_id,))
                    for row in cur.fetchall():
                        occupied_slots.add(row[0])

                    slot_to_use = None
                    for s in range(1, 10):
                        if s not in occupied_slots:
                            slot_to_use = s
                            break

                    if not slot_to_use:
                        cur.close()
                        return jsonify(
                            {'success': False, 'message': 'Inventario lleno. No hay slots disponibles.'}), 409

                cur.execute("INSERT INTO Inventario (personaje_id, item_id, cantidad, slot) VALUES (%s, %s, %s, %s)",
                            (character_id, item_id, cantidad_a_anadir, slot_to_use))
                mysql.connection.commit()
                cur.close()
                return jsonify({'success': True,
                                'message': f'{cantidad_a_anadir} {item_nombre}(s) añadidos al slot {slot_to_use}.',
                                'data': {'slot': slot_to_use}}), 201

        else:
            slot_to_use = slot_sugerido
            if not slot_to_use:
                occupied_slots = set()
                cur.execute("SELECT slot FROM Inventario WHERE personaje_id = %s", (character_id,))
                for row in cur.fetchall():
                    occupied_slots.add(row[0])

                slot_to_use = None
                for s in range(1, 10):
                    if s not in occupied_slots:
                        slot_to_use = s
                        break

                if not slot_to_use:
                    cur.close()
                    return jsonify({'success': False,
                                    'message': 'Inventario lleno. No hay slots disponibles para este ítem no apilable.'}), 409

            else:
                cur.execute("SELECT id FROM Inventario WHERE personaje_id = %s AND slot = %s",
                            (character_id, slot_to_use))
                if cur.fetchone():
                    cur.close()
                    return jsonify(
                        {'success': False, 'message': f'El slot {slot_to_use} ya está ocupado por otro ítem.'}), 409

            cur.execute("INSERT INTO Inventario (personaje_id, item_id, cantidad, slot) VALUES (%s, %s, %s, %s)",
                        (character_id, item_id, 1, slot_to_use))
            mysql.connection.commit()
            cur.close()
            return jsonify(
                {'success': True, 'message': f'{item_nombre} añadido al slot {slot_to_use}.',
                 'data': {'slot': slot_to_use}}), 201

    except Exception as e:
        print(f"Error en add_item_to_inventory: {e}")
        return jsonify({'success': False, 'message': 'Error interno del servidor al añadir ítem.'}), 500


# --- Endpoint para Enemigos Derrotados ---
@app.route('/api/enemies_defeated/<int:character_id>', methods=['GET'])
def get_enemies_defeated(character_id):
    """
    Endpoint para obtener la lista de enemigos derrotados para un personaje,
    incluyendo el conteo de veces que cada tipo de enemigo ha sido derrotado.
    Alineado con el esquema de tus tablas EnemigosDerrotados y Enemigos.
    """
    try:
        cur = mysql.connection.cursor()
        cur.execute("""
            SELECT
                ed.enemigo_id,
                COUNT(ed.id) AS defeated_count,
                e.nombre AS enemy_name,
                e.descripcion AS enemy_description,
                e.resistencia AS enemy_hp_base,
                e.daño AS enemy_attack_base,
                e.tipo_ataque AS enemy_attack_type,
                e.tipo_enemigo AS enemy_type,
                e.habilidad_especial AS enemy_special_ability,
                e.icono_url AS enemy_icon_url
            FROM EnemigosDerrotados ed
            JOIN Enemigos e ON ed.enemigo_id = e.id
            WHERE ed.personaje_id = %s
            GROUP BY ed.enemigo_id, e.nombre, e.descripcion, e.resistencia, e.daño, e.tipo_ataque, e.tipo_enemigo, e.habilidad_especial, e.icono_url
            ORDER BY defeated_count DESC
        """, (character_id,))
        enemies_data = cur.fetchall()
        cur.close()

        enemies_list = []
        for enemy in enemies_data:
            enemy_entry = {
                'enemy_id': enemy[0],
                'defeated_count': enemy[1],
                'enemy_name': enemy[2],
                'enemy_description': enemy[3],
                'enemy_hp_base': enemy[4],
                'enemy_attack_base': enemy[5],
                'enemy_attack_type': enemy[6],
                'enemy_type': enemy[7],
                'enemy_special_ability': enemy[8],
                'enemy_icon_url': enemy[9]
            }
            enemies_list.append(enemy_entry)

        return jsonify({'success': True, 'message': 'Enemigos derrotados cargados.', 'data': enemies_list}), 200

    except Exception as e:
        print(f"Error en get_enemies_defeated: {e}")
        return jsonify(
            {'success': False, 'message': 'Error interno del servidor al cargar enemigos derrotados: ' + str(e)}), 500


# --- ¡NUEVAS RUTAS PARA EL RESTABLECIMIENTO DE CONTRASEÑA! ---

@app.route('/api/request_password_reset_code', methods=['POST'])
def request_password_reset_code():
    """
    Endpoint para solicitar un código de restablecimiento de contraseña.
    Genera un código, lo almacena en la DB y lo envía por correo.
    """
    data = request.get_json()
    email = data.get('email')

    if not email:
        return jsonify({"success": False, "message": "Correo electrónico es requerido."}), 400

    try:
        cur = mysql.connection.cursor()
        cur.execute("SELECT id, nombre_usuario FROM Usuarios WHERE correo = %s", (email,))
        user_data = cur.fetchone()

        if not user_data:
            # Por seguridad, no reveles si el correo existe o no.
            # Devuelve un mensaje genérico de éxito aunque el correo no esté registrado.
            cur.close()
            return jsonify({"success": True,
                            "message": "Si tu correo está registrado, recibirás un código de restablecimiento."}), 200

        user_id, nombre_usuario = user_data

        # Invalidar tokens antiguos no usados para este usuario
        cur.execute("UPDATE PasswordResetTokens SET is_used = TRUE WHERE user_id = %s AND is_used = FALSE", (user_id,))
        mysql.connection.commit()

        # Generar un nuevo código y establecer expiración (ej. 10 minutos)
        code = generate_reset_code()
        expires_at = datetime.now() + timedelta(minutes=10)

        # Insertar el nuevo token en la tabla PasswordResetTokens
        cur.execute(
            "INSERT INTO PasswordResetTokens (user_id, token, expires_at) VALUES (%s, %s, %s)",
            (user_id, code, expires_at)
        )
        mysql.connection.commit()
        cur.close()

        # Enviar el correo electrónico
        email_subject = "Código de Restablecimiento de Contraseña para VEILWALKER"
        email_body = f"Hola {nombre_usuario},\n\nTu código de restablecimiento de contraseña es: {code}\n\nEste código es válido por 10 minutos.\n\nSi no solicitaste este restablecimiento, por favor ignora este correo.\n\nAtentamente,\nEl equipo de VEILWALKER"

        if send_email(email, email_subject, email_body):
            return jsonify({"success": True, "message": "Código de restablecimiento enviado a tu correo."}), 200
        else:
            return jsonify({"success": False,
                            "message": "Error al enviar el código de restablecimiento. Intenta de nuevo más tarde."}), 500

    except Exception as e:
        print(f"Error en request_password_reset_code: {e}")
        return jsonify(
            {'success': False, 'message': 'Error interno del servidor durante la solicitud de restablecimiento.'}), 500


@app.route('/api/reset_password_with_code', methods=['POST'])
def reset_password_with_code():
    """
    Endpoint para restablecer la contraseña de un usuario usando un código de verificación.
    """
    data = request.get_json()
    email = data.get('email')
    code = data.get('code')
    new_password = data.get('new_password')

    if not all([email, code, new_password]):
        return jsonify(
            {"success": False, "message": "Todos los campos (correo, código, nueva contraseña) son requeridos."}), 400

    try:
        cur = mysql.connection.cursor()
        cur.execute("SELECT id FROM Usuarios WHERE correo = %s", (email,))
        user_data = cur.fetchone()

        if not user_data:
            cur.close()
            return jsonify({"success": False, "message": "Correo electrónico no encontrado."}), 404

        user_id = user_data[0]

        # Buscar el token más reciente y no usado para este usuario y código
        cur.execute(
            "SELECT id, expires_at, is_used FROM PasswordResetTokens WHERE user_id = %s AND token = %s ORDER BY created_at DESC LIMIT 1",
            (user_id, code)
        )
        token_entry = cur.fetchone()

        if not token_entry:
            cur.close()
            return jsonify({"success": False, "message": "Código inválido o incorrecto."}), 400

        token_id, expires_at, is_used = token_entry

        if is_used:
            cur.close()
            return jsonify({"success": False, "message": "Este código ya ha sido utilizado."}), 400

        if expires_at < datetime.now():
            cur.close()
            return jsonify({"success": False, "message": "Código expirado."}), 400

        # Si el token es válido, procede a actualizar la contraseña
        hashed_new_password = bcrypt.generate_password_hash(new_password).decode('utf-8')
        cur.execute("UPDATE Usuarios SET password = %s WHERE id = %s", (hashed_new_password, user_id))

        # Marcar el token como usado
        cur.execute("UPDATE PasswordResetTokens SET is_used = TRUE WHERE id = %s", (token_id,))

        mysql.connection.commit()
        cur.close()

        return jsonify({"success": True, "message": "Contraseña restablecida exitosamente."}), 200

    except Exception as e:
        print(f"Error en reset_password_with_code: {e}")
        return jsonify({'success': False, 'message': 'Error interno del servidor al restablecer contraseña.'}), 500


# --- Ejecutar la aplicación Flask ---
if __name__ == '__main__':
    app.run(debug=True, port=5000)
