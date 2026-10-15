package br.com.aspas.contingencia.common
object Uuid { fun new(): String = java.util.UUID.randomUUID().toString() }
