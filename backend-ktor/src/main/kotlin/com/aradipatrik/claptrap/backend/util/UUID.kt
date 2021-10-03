package com.aradipatrik.claptrap.backend.util

import java.util.UUID

fun String.toUuid() = UUID.fromString(this)