import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/game.dart';
import 'auth_service.dart';

class ApiService {
  static const String baseUrl =
      'https://game-tracker-wohl.onrender.com/api/juegos';
  // Para desarrollo local:
  // static const String baseUrl = 'http://localhost:8080/api/juegos';

  final AuthService _authService = AuthService();

  /// Construye los headers con el Bearer token si hay sesión activa.
  /// Mismo patrón que _getHeaders() en TweetService de Tweeter.
  Map<String, String> _getHeaders({bool withBody = false}) {
    final headers = <String, String>{};
    if (withBody) headers['Content-Type'] = 'application/json';

    final token = _authService.getToken();
    if (token != null && token.isNotEmpty) {
      headers['Authorization'] = 'Bearer $token';
    }
    return headers;
  }

  // ── GET: Obtener todos ───────────────────────────────────────────────────
  Future<List<Game>> fetchGames() async {
    await _authService.init();
    final response = await http.get(
      Uri.parse(baseUrl),
      headers: _getHeaders(),
    );
    if (response.statusCode == 200) {
      final data = json.decode(response.body) as List<dynamic>;
      return data.map((j) => Game.fromJson(j as Map<String, dynamic>)).toList();
    }
    throw Exception('Error al cargar juegos (${response.statusCode})');
  }

  // ── POST: Crear nuevo ────────────────────────────────────────────────────
  Future<void> createGame(Game game) async {
    await _authService.init();
    final response = await http.post(
      Uri.parse(baseUrl),
      headers: _getHeaders(withBody: true),
      body: json.encode(game.toJson()),
    );
    if (response.statusCode != 200 && response.statusCode != 201) {
      throw Exception('Error al crear el juego (${response.statusCode})');
    }
  }

  // ── PUT: Actualizar juego ────────────────────────────────────────────────
  Future<void> updateGame(int id, Game game) async {
    await _authService.init();
    final response = await http.put(
      Uri.parse('$baseUrl/$id'),
      headers: _getHeaders(withBody: true),
      body: json.encode(game.toJson()),
    );
    if (response.statusCode != 200 &&
        response.statusCode != 201 &&
        response.statusCode != 204) {
      throw Exception(
          'Error al actualizar el juego (${response.statusCode})');
    }
  }

  // ── PUT: Editar nota ─────────────────────────────────────────────────────
  Future<void> updateGameNote(int gameId, int noteIndex, GameNote note) async {
    await _authService.init();
    final response = await http.put(
      Uri.parse('$baseUrl/$gameId/notes/$noteIndex'),
      headers: _getHeaders(withBody: true),
      body: json.encode(note.toJson()),
    );
    if (response.statusCode != 200 &&
        response.statusCode != 201 &&
        response.statusCode != 204) {
      throw Exception('Error al editar la nota (${response.statusCode})');
    }
  }

  // ── DELETE: Borrar nota ──────────────────────────────────────────────────
  Future<void> deleteGameNote(int gameId, int noteIndex) async {
    await _authService.init();
    final response = await http.delete(
      Uri.parse('$baseUrl/$gameId/notes/$noteIndex'),
      headers: _getHeaders(),
    );
    if (response.statusCode != 200 && response.statusCode != 204) {
      throw Exception('Error al borrar la nota (${response.statusCode})');
    }
  }

  // ── DELETE: Borrar juego ─────────────────────────────────────────────────
  Future<void> deleteGame(int id) async {
    await _authService.init();
    final response = await http.delete(
      Uri.parse('$baseUrl/$id'),
      headers: _getHeaders(),
    );
    if (response.statusCode != 200 && response.statusCode != 204) {
      throw Exception('Error al eliminar el juego (${response.statusCode})');
    }
  }
}