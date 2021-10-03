package com.aradipatrik.claptrap.fakeinteractors.generators

import kotlin.random.Random

internal object LoremIpsum {
  private val loremIpsumText = """
    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean viverra eleifend pellentesque. 
    Sed faucibus lobortis mi, ac suscipit nibh feugiat non. Donec vel aliquam velit. Aenean in 
    tortor eu justo pretium consequat. Proin malesuada tempus nibh a sodales. Aliquam ornare, nisl 
    at accumsan varius, felis justo ultricies libero, id gravida mi dolor vitae arcu. Vestibulum 
    quis ipsum porta, pretium justo quis, posuere velit. Mauris quam libero, feugiat a nulla eu, 
    volutpat malesuada purus. Donec vel ex vel nunc imperdiet ultrices fringilla sed nisl. 
    Nullam nisi nunc, sagittis eget sem quis, mollis iaculis dolor. Curabitur aliquam efficitur 
    nunc, eget lobortis lacus sodales nec. Aenean bibendum vel quam pharetra malesuada. 
    Nulla sodales urna id imperdiet facilisis. 
  """.trimIndent()

  internal fun Random.nextWords(count: Int = 5, capitalize: Boolean = false) = loremIpsumText
    .replace(",", "")
    .replace(".", "")
    .lines()
    .flatMap { it.split(" ") }
    .shuffled()
    .filter { it.isNotBlank() }
    .take(count)
    .joinToString(separator = " ")
    .trim()
    .capitalize(capitalize)

  internal fun Random.nextCapitalWord() = nextWords(count = 1, capitalize = true)

  internal fun Random.nextSmallCaptionWord() = nextWords(count = 1, capitalize = false)

  private fun String.capitalize(capital: Boolean) = if (capital) {
    capitalize()
  } else {
    decapitalize()
  }
}
