package com.um.proxy.asientos;

public enum AsientoEstado {
    LIBRE,
    BLOQUEADO,
    VENDIDO;

    public static AsientoEstado fromRedisValue(String v) {
        if (v == null) return LIBRE;

        String s = v.trim().toUpperCase();
        return switch (s) {
            case "LIBRE", "FREE", "0" -> LIBRE;
            case "BLOQUEADO", "LOCKED", "1" -> BLOQUEADO;
            case "VENDIDO", "SOLD", "2" -> VENDIDO;
            default -> LIBRE; // fallback seguro
        };
    }
}
