/*
* $Id$
*
* Copyright 2016 Valentyn Kolesnikov
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.moneytostr
/**
* Converts numbers to symbols.
*
* @author Valentyn Kolesnikov
* @version $Revision$ $Date$
*/
class MoneyToStr {
  val messages:Map<String, Array<String>> = java.util.LinkedHashMap<String, Array<String>>()
  private val rubOneUnit:String
  private val rubTwoUnit:String
  private val rubFiveUnit:String
  private val rubSex:String
  private val kopOneUnit:String
  private val kopTwoUnit:String
  private val kopFiveUnit:String
  private val kopSex:String
  val rubShortUnit:String
  private val currency:Currency
  val language:Language
  private val pennies:Pennies
  /** Currency. */
  enum class Currency {
    /**.*/
    RUR,
    /**.*/
    UAH,
    /**.*/
    USD,
    /**.*/
    PER10,
    /**.*/
    PER100,
    /**.*/
    PER1000,
    /**.*/
    PER10000,
    /**.*/
    Custom
  }
  /** Language. */
  enum class Language {
    /**.*/
    RUS,
    /**.*/
    UKR,
    /**.*/
    ENG
  }
  /** Pennies. */
  enum class Pennies {
    /**.*/
    NUMBER,
    /**.*/
    TEXT
  }
  /**
 * Inits class with currency. Usage: MoneyToStr moneyToStr = new MoneyToStr(
 * MoneyToStr.Currency.UAH, MoneyToStr.Language.UKR, MoneyToStr.Pennies.NUMBER);
 * Definition for currency is placed into currlist.xml
 *
 * @param currency the currency (UAH, RUR, USD)
 * @param language the language (UKR, RUS, ENG)
 * @param pennies the pennies (NUMBER, TEXT)
 */
  constructor(currency:Currency, language:Language, pennies:Pennies) {
    this.currency = currency
    this.language = language
    this.pennies = pennies
    val theISOstr = currency.name
    val languageElement = (xmlDoc.getElementsByTagName(language.name)).item(0) as org.w3c.dom.Element
    val items = languageElement.getElementsByTagName("item")
    run({ var index = 0
         while (index < items.getLength())
         {
           val languageItem = items.item(index) as org.w3c.dom.Element
           messages.put(languageItem.getAttribute("value"), languageItem.getAttribute("text").split((",").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray())
           index += 1
         } })
    val theISOElements = (xmlDoc.getElementsByTagName(theISOstr)) as org.w3c.dom.NodeList
    var theISOElement:org.w3c.dom.Element = null
    var index = 0
    while (index < theISOElements.getLength())
    {
      if ((theISOElements.item(index) as org.w3c.dom.Element).getAttribute("language") == language.name)
      {
        theISOElement = theISOElements.item(index) as org.w3c.dom.Element
        break
      }
      index += 1
    }
    rubOneUnit = theISOElement.getAttribute("RubOneUnit")
    rubTwoUnit = theISOElement.getAttribute("RubTwoUnit")
    rubFiveUnit = theISOElement.getAttribute("RubFiveUnit")
    kopOneUnit = theISOElement.getAttribute("KopOneUnit")
    kopTwoUnit = theISOElement.getAttribute("KopTwoUnit")
    kopFiveUnit = theISOElement.getAttribute("KopFiveUnit")
    rubSex = theISOElement.getAttribute("RubSex")
    kopSex = theISOElement.getAttribute("KopSex")
    rubShortUnit = if (theISOElement.hasAttribute("RubShortUnit")) theISOElement.getAttribute("RubShortUnit") else ""
  }
  /**
 * Inits class with currency. Usage: MoneyToStr moneyToStr = new MoneyToStr(
 * MoneyToStr.Currency.UAH, MoneyToStr.Language.UKR, MoneyToStr.Pennies.NUMBER);
 *
 * @param currency the currency (UAH, RUR, USD)
 * @param language the language (UKR, RUS, ENG)
 * @param pennies the pennies (NUMBER, TEXT)
 * @param names the custom names
 */
  constructor(currency:Currency, language:Language, pennies:Pennies, names:Array<String>) {
    if (currency == null)
    {
      throw IllegalArgumentException("currency is null")
    }
    if (language == null)
    {
      throw IllegalArgumentException("language is null")
    }
    if (pennies == null)
    {
      throw IllegalArgumentException("pennies is null")
    }
    if (names == null || names.size != 8)
    {
      throw IllegalArgumentException("names is null")
    }
    this.currency = currency
    this.language = language
    this.pennies = pennies
    val languageElement = (xmlDoc.getElementsByTagName(language.name)).item(0) as org.w3c.dom.Element
    val items = languageElement.getElementsByTagName("item")
    var index = 0
    while (index < items.getLength())
    {
      val languageItem = items.item(index) as org.w3c.dom.Element
      messages.put(languageItem.getAttribute("value"), languageItem.getAttribute("text").split((",").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray())
      index += 1
    }
    rubOneUnit = names[0]
    rubTwoUnit = names[1]
    rubFiveUnit = names[2]
    rubSex = names[3]
    kopOneUnit = names[4]
    kopTwoUnit = names[5]
    kopFiveUnit = names[6]
    kopSex = names[7]
    rubShortUnit = names[0]
  }
  /**
 * Converts double value to the text description.
 *
 * @param theMoney
 * the amount of money in format major.minor
 * @return the string description of money value
 */
  fun convert(theMoney:Double):String {
    if (theMoney == null)
    {
      throw IllegalArgumentException("theMoney is null")
    }
    val intPart = theMoney.toLong()
    var fractPart = Math.round((theMoney - intPart) * NUM100)
    if (currency == Currency.PER1000)
    {
      fractPart = Math.round((theMoney - intPart) * NUM1000)
    }
    return convert(intPart, fractPart)
  }
  /**
 * Converts amount to words. Usage: MoneyToStr moneyToStr =
 * new MoneyToStr(MoneyToStr.Currency.UAH, MoneyToStr.Language.UKR, MoneyToStr.Pennies.NUMBER);
 * String result = moneyToStr.convert(123D); Expected: result = сто двадцять три гривні 00 копійок
 *
 * @param theMoney
 * the amount of money major currency
 * @param theKopeiki
 * the amount of money minor currency
 * @return the string description of money value
 */
  fun convert(theMoney:Long, theKopeiki:Long):String {
    if (theMoney == null)
    {
      throw IllegalArgumentException("theMoney is null")
    }
    if (theKopeiki == null)
    {
      throw IllegalArgumentException("theKopeiki is null")
    }
    val money2str = StringBuilder()
    val triadNum = 0L
    val theTriad:Long
    val intPart = theMoney
    if (intPart === 0)
    {
      money2str.append(messages.get("0")[0] + " ")
    }
    do
    {
      theTriad = intPart % NUM1000
      money2str.insert(0, triad2Word(theTriad, triadNum, rubSex))
      if (triadNum === 0)
      {
        if ((theTriad % NUM100) / NUM10 == NUM1.toLong())
        {
          money2str.append(rubFiveUnit)
        }
        else
        {
          when (java.lang.Long.valueOf(theTriad % NUM10).toByte()) {
            NUM1 -> money2str.append(rubOneUnit)
            NUM2, NUM3, NUM4 -> money2str.append(rubTwoUnit)
            else -> money2str.append(rubFiveUnit)
          }
        }
      }
      intPart /= NUM1000.toLong()
      triadNum++
    }
    while (intPart > 0)
    if (pennies == Pennies.TEXT)
    {
      money2str.append(if (language == Language.ENG) " and " else " ").append(
        if (theKopeiki === 0) messages.get("0")[0] + " " else triad2Word(theKopeiki, 0L, kopSex))
    }
    else
    {
      money2str.append(" " + (if (theKopeiki < 10) "0" + theKopeiki else theKopeiki) + " ")
    }
    if (theKopeiki >= NUM11 && theKopeiki <= NUM14)
    {
      money2str.append(kopFiveUnit)
    }
    else
    {
      when ((theKopeiki % NUM10).toByte()) {
        NUM1 -> money2str.append(kopOneUnit)
        NUM2, NUM3, NUM4 -> money2str.append(kopTwoUnit)
        else -> money2str.append(kopFiveUnit)
      }
    }
    return money2str.toString().trim({ it <= ' ' })
  }
  private fun triad2Word(triad:Long, triadNum:Long, sex:String):String {
    val triadWord = StringBuilder(NUM100)
    if (triad === 0)
    {
      return ""
    }
    triadWord.append(concat<String>(arrayOf<String>(""), messages.get("100_900"))[java.lang.Long.valueOf(triad / NUM100).toByte()])
    val range10 = (triad % NUM100) / NUM10
    triadWord.append(concat<String>(arrayOf<String>("", ""), messages.get("20_90"))[range10.toByte()])
    if (language == Language.ENG && triadWord.length > 0 && triad % NUM10 == 0)
    {
      triadWord.deleteCharAt(triadWord.length - 1)
      triadWord.append(" ")
    }
    check2(triadNum, sex, triadWord, triad, range10)
    when (triadNum.toByte()) {
      NUM0 -> {}
      NUM1, NUM2, NUM3, NUM4 -> if (range10 === NUM1)
      {
        triadWord.append(messages.get("1000_10")[triadNum.toByte() - 1] + " ")
      }
      else
      {
        val range = triad % NUM10
        when (range.toByte()) {
          NUM1 -> triadWord.append(messages.get("1000_1")[triadNum.toByte() - 1] + " ")
          NUM2, NUM3, NUM4 -> triadWord.append(messages.get("1000_234")[triadNum.toByte() - 1] + " ")
          else -> triadWord.append(messages.get("1000_5")[triadNum.toByte() - 1] + " ")
        }
      }
      else -> triadWord.append("??? ")
    }
    return triadWord.toString()
  }
  /**
 * @param triadNum the triad num
 * @param sex the sex
 * @param triadWord the triad word
 * @param triad the triad
 * @param range10 the range 10
 */
  private fun check2(triadNum:Long, sex:String, triadWord:StringBuilder, triad:Long, range10:Long) {
    val range = triad % NUM10
    if (range10 === 1)
    {
      triadWord.append(messages.get("10_19")[range.toByte()] + " ")
    }
    else
    {
      when (range.toByte()) {
        NUM1 -> if (triadNum === NUM1)
        {
          triadWord.append(messages.get("1")[INDEX_0] + " ")
        }
        else if (triadNum === NUM2 || triadNum === NUM3 || triadNum === NUM4)
        {
          triadWord.append(messages.get("1")[INDEX_1] + " ")
        }
        else if ("M" == sex)
        {
          triadWord.append(messages.get("1")[INDEX_2] + " ")
        }
        else if ("F" == sex)
        {
          triadWord.append(messages.get("1")[INDEX_3] + " ")
        }
        NUM2 -> if (triadNum === NUM1)
        {
          triadWord.append(messages.get("2")[INDEX_0] + " ")
        }
        else if (triadNum === NUM2 || triadNum === NUM3 || triadNum === NUM4)
        {
          triadWord.append(messages.get("2")[INDEX_1] + " ")
        }
        else if ("M" == sex)
        {
          triadWord.append(messages.get("2")[INDEX_2] + " ")
        }
        else if ("F" == sex)
        {
          triadWord.append(messages.get("2")[INDEX_3] + " ")
        }
        NUM3, NUM4, NUM5, NUM6, NUM7, NUM8, NUM9 -> triadWord.append(concat<String>(arrayOf<String>("", "", ""), messages.get("3_9"))[range.toByte()] + " ")
        else -> {}
      }
    }
  }
  private fun <T> concat(first:Array<T>, second:Array<T>):Array<T> {
    val result = java.util.Arrays.copyOf<T>(first, first.size + second.size)
    System.arraycopy(second, 0, result, first.size, second.size)
    return result
  }
  companion object {
    private val INDEX_3 = 3
    private val INDEX_2 = 2
    private val INDEX_1 = 1
    private val INDEX_0 = 0
    private var xmlDoc:org.w3c.dom.Document
    private val NUM0 = 0
    private val NUM1 = 1
    private val NUM2 = 2
    private val NUM3 = 3
    private val NUM4 = 4
    private val NUM5 = 5
    private val NUM6 = 6
    private val NUM7 = 7
    private val NUM8 = 8
    private val NUM9 = 9
    private val NUM10 = 10
    private val NUM11 = 11
    private val NUM14 = 14
    private val NUM100 = 100
    private val NUM1000 = 1000
    private val NUM10000 = 10000
    private val CURRENCY_LIST =
    """<CurrencyList>
 
    <language value="UKR"/>
    <UKR>
        <item value="0" text="нуль"/>
        <item value="1000_10" text="тисяч,мільйонів,мільярдів,трильйонів"/>
        <item value="1000_1" text="тисяча,мільйон,мільярд,трильйон"/>
        <item value="1000_234" text="тисячі,мільйона,мільярда,трильйона"/>
        <item value="1000_5" text="тисяч,мільйонів,мільярдів,трильйонів"/>
        <item value="10_19" text="десять,одинадцять,дванадцять,тринадцять,чотирнадцять,п’ятнадцять,шiстнадцять,сiмнадцять,вiсiмнадцять,дев'ятнадцять"/>
        <item value="1" text="одна,один,один,одна"/>
        <item value="2" text="дві,два,два,дві"/>
        <item value="3_9" text="три,чотири,п’ять,шість,сім,вісім,дев’ять"/>
        <item value="100_900" text="сто ,двісті ,триста ,чотириста ,п’ятсот ,шістсот ,сімсот ,вісімсот ,дев’ятсот "/>
        <item value="20_90" text="двадцять ,тридцять ,сорок ,п’ятдесят ,шістдесят ,сімдесят ,вісімдесят ,дев’яносто "/>
        <item value="pdv" text="в т.ч. ПДВ "/>
        <item value="pdv_value" text="20"/>
    </UKR>
    <RUS>
        <item value="0" text="ноль"/>
        <item value="1000_10" text="тысяч,миллионов,миллиардов,триллионов"/>
        <item value="1000_1" text="тысяча,миллион,миллиард,триллион"/>
        <item value="1000_234" text="тысячи,миллиона,миллиарда,триллиона"/>
        <item value="1000_5" text="тысяч,миллионов,миллиардов,триллионов"/>
        <item value="10_19" text="десять,одиннадцать,двенадцать,тринадцать,четырнадцать,пятнадцать,шестнадцать,семнадцать,восемнадцать,девятнадцать"/>
        <item value="1" text="одна,один,один,одна"/>
        <item value="2" text="две,два,два,две"/>
        <item value="3_9" text="три,четыре,пять,шесть,семь,восемь,девять"/>
        <item value="100_900" text="сто ,двести ,триста ,четыреста ,пятьсот ,шестьсот ,семьсот ,восемьсот ,девятьсот "/>
        <item value="20_90" text="двадцать ,тридцать ,сорок ,пятьдесят ,шестьдесят ,семьдесят ,восемьдесят ,девяносто "/>
        <item value="pdv" text="в т.ч. НДС "/>
        <item value="pdv_value" text="18"/>
    </RUS>
    <ENG>
        <item value="0" text="zero"/>
        <item value="1000_10" text="thousand,million,billion,trillion"/>
        <item value="1000_1" text="thousand,million,billion,trillion"/>
        <item value="1000_234" text="thousand,million,billion,trillion"/>
        <item value="1000_5" text="thousand,million,billion,trillion"/>
        <item value="10_19" text="ten,eleven,twelve,thirteen,fourteen,fifteen,sixteen,seventeen,eighteen,nineteen"/>
        <item value="1" text="one,one,one,one"/>
        <item value="2" text="two,two,two,two"/>
        <item value="3_9" text="three,four,five,six,seven,eight,nine"/>
        <item value="100_900" text="one hundred ,two hundred ,three hundred ,four hundred ,five hundred ,six hundred ,seven hundred ,eight hundred ,nine hundred "/>
        <item value="20_90" text="twenty-,thirty-,forty-,fifty-,sixty-,seventy-,eighty-,ninety-"/>
        <item value="pdv" text="including VAT "/>
        <item value="pdv_value" text="10"/>
    </ENG>

    <RUR CurrID="810" CurrName="Российские рубли" language="RUS"
         RubOneUnit="рубль" RubTwoUnit="рубля" RubFiveUnit="рублей" RubSex="M" RubShortUnit="руб."
         KopOneUnit="копейка" KopTwoUnit="копейки" KopFiveUnit="копеек" KopSex="F"
    />
    <UAH CurrID="980" CurrName="Украинскі гривні" language="RUS"
         RubOneUnit="гривня" RubTwoUnit="гривни" RubFiveUnit="гривень" RubSex="F" RubShortUnit="грн."
         KopOneUnit="копейка" KopTwoUnit="копейки" KopFiveUnit="копеек" KopSex="F"
    />
    <USD CurrID="840" CurrName="Долари США" language="RUS"
         RubOneUnit="доллар" RubTwoUnit="доллара" RubFiveUnit="долларов" RubSex="M" RubShortUnit="дол."
         KopOneUnit="цент" KopTwoUnit="цента" KopFiveUnit="центов" KopSex="M"
    />

    <RUR CurrID="810" CurrName="Российские рубли" language="UKR"
         RubOneUnit="рубль" RubTwoUnit="рублі" RubFiveUnit="рублів" RubSex="M" RubShortUnit="руб."
         KopOneUnit="копійка" KopTwoUnit="копійки" KopFiveUnit="копійок" KopSex="F"
    /> 
    <UAH CurrID="980" CurrName="Украинскі гривні" language="UKR"
         RubOneUnit="гривня" RubTwoUnit="гривні" RubFiveUnit="гривень" RubSex="F" RubShortUnit="грн."
         KopOneUnit="копійка" KopTwoUnit="копійки" KopFiveUnit="копійок" KopSex="F"
    />
    <USD CurrID="840" CurrName="Долари США" language="UKR"
         RubOneUnit="долар" RubTwoUnit="долара" RubFiveUnit="доларів" RubSex="M" RubShortUnit="дол."
         KopOneUnit="цент" KopTwoUnit="цента" KopFiveUnit="центів" KopSex="M"
    />

    <RUR CurrID="810" CurrName="Российские рубли" language="ENG"
         RubOneUnit="ruble" RubTwoUnit="rubles" RubFiveUnit="rubles" RubSex="M" RubShortUnit="RUR."
         KopOneUnit="kopeck" KopTwoUnit="kopecks" KopFiveUnit="kopecks" KopSex="M"
    /> 
    <UAH CurrID="980" CurrName="Украинскі гривні" language="ENG"
         RubOneUnit="hryvnia" RubTwoUnit="hryvnias" RubFiveUnit="hryvnias" RubSex="M" RubShortUnit="UAH."
         KopOneUnit="kopeck" KopTwoUnit="kopecks" KopFiveUnit="kopecks" KopSex="M"
    />
    <USD CurrID="840" CurrName="Долари США" language="ENG"
         RubOneUnit="dollar" RubTwoUnit="dollars" RubFiveUnit="dollars" RubSex="M" RubShortUnit="USD."
         KopOneUnit="cent" KopTwoUnit="cents" KopFiveUnit="cents" KopSex="M"
    />

    <PER10 CurrID="556" CurrName="Вiдсотки з десятими частинами" language="RUS"
         RubOneUnit="целая," RubTwoUnit="целых," RubFiveUnit="целых," RubSex="F"
         KopOneUnit="десятая процента" KopTwoUnit="десятых процента" KopFiveUnit="десятых процента" KopSex="F"
    />

    <PER100 CurrID="557" CurrName="Вiдсотки з сотими частинами" language="RUS"
         RubOneUnit="целая," RubTwoUnit="целых," RubFiveUnit="целых," RubSex="F"
         KopOneUnit="сотая процента" KopTwoUnit="сотых процента" KopFiveUnit="сотых процента" KopSex="F"
    />

    <PER1000 CurrID="558" CurrName="Вiдсотки з тисячними частинами" language="RUS"
         RubOneUnit="целая," RubTwoUnit="целых," RubFiveUnit="целых," RubSex="F"
         KopOneUnit="тысячная процента" KopTwoUnit="тысячных процента" KopFiveUnit="тысячных процента" KopSex="F"
    />

    <PER10000 CurrID="559" CurrName="Вiдсотки з десяти тисячними частинами" language="RUS"
         RubOneUnit="целая," RubTwoUnit="целых," RubFiveUnit="целых," RubSex="F"
         KopOneUnit="десятитысячная процента" KopTwoUnit="десятитысячные процента" KopFiveUnit="десятитысячных процента" KopSex="F"
    />

    <PER10 CurrID="556" CurrName="Вiдсотки з десятими частинами" language="UKR"
         RubOneUnit="ціла," RubTwoUnit="цілих," RubFiveUnit="цілих," RubSex="F"
         KopOneUnit="десята відсотка" KopTwoUnit="десятих відсотка" KopFiveUnit="десятих відсотка" KopSex="F"
    />

    <PER100 CurrID="557" CurrName="Вiдсотки з сотими частинами" language="UKR"
         RubOneUnit="ціла," RubTwoUnit="цілих," RubFiveUnit="цілих," RubSex="F"
         KopOneUnit="сота відсотка" KopTwoUnit="сотих відсотка" KopFiveUnit="сотих відсотка" KopSex="F"
    />

    <PER1000 CurrID="558" CurrName="Вiдсотки з тисячними частинами" language="UKR"
         RubOneUnit="ціла," RubTwoUnit="цілих," RubFiveUnit="цілих," RubSex="F"
         KopOneUnit="тисячна відсотка" KopTwoUnit="тисячних відсотка" KopFiveUnit="тисячних відсотка" KopSex="F"
    />

    <PER10000 CurrID="559" CurrName="Вiдсотки з десяти тисячними частинами" language="UKR"
         RubOneUnit="ціла," RubTwoUnit="цілих," RubFiveUnit="цілих," RubSex="F"
         KopOneUnit="десятитисячна відсотка" KopTwoUnit="десятитисячних відсотка" KopFiveUnit="десятитисячних відсотка" KopSex="M"
    />

    <PER10 CurrID="560" CurrName="Вiдсотки з десятими частинами" language="ENG"
         RubOneUnit="," RubTwoUnit="integers," RubFiveUnit="integers," RubSex="F"
         KopOneUnit="tenth of one percent" KopTwoUnit="tenth of one percent" KopFiveUnit="tenth of one percent" KopSex="F"
    />

    <PER100 CurrID="561" CurrName="Вiдсотки з сотими частинами" language="ENG"
         RubOneUnit="," RubTwoUnit="integers," RubFiveUnit="integers," RubSex="F"
         KopOneUnit="hundred percent" KopTwoUnit="hundredth of percent" KopFiveUnit="hundredth of percent" KopSex="F"
    />

    <PER1000 CurrID="562" CurrName="Вiдсотки з тисячними частинами" language="ENG"
         RubOneUnit="," RubTwoUnit="integers," RubFiveUnit="integers," RubSex="F"
         KopOneUnit="thousandth of percent" KopTwoUnit="thousandths of percent" KopFiveUnit="thousandths of percent" KopSex="F"
    />

    <PER10000 CurrID="563" CurrName="Вiдсотки з десяти тисячними частинами" language="ENG"
         RubOneUnit="," RubTwoUnit="integers," RubFiveUnit="integers," RubSex="F"
         KopOneUnit="ten percent" KopTwoUnit="ten-percent" KopFiveUnit="ten-percent" KopSex="F"
    />

</CurrencyList>"""
    init{
      initXmlDoc(CURRENCY_LIST)
    }
    fun initXmlDoc(xmlData:String) {
      val docFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
      try
      {
        val xmlDocBuilder = docFactory.newDocumentBuilder()
        xmlDoc = xmlDocBuilder.parse(java.io.ByteArrayInputStream(xmlData.toByteArray(charset("UTF8"))))
      }
      catch (ex:Exception) {
        throw UnsupportedOperationException(ex)
      }
    }
    /**
 * Converts percent to string.
 * @param amount the amount of percent
 * @param lang the language (RUS, UKR, ENG)
 * @param pennies the pennies (NUMBER, TEXT)
 * @return the string of percent
 */
    @JvmOverloads fun percentToStr(amount:Double, lang:Language, pennies:Pennies = Pennies.TEXT):String {
      if (amount == null)
      {
        throw IllegalArgumentException("amount is null")
      }
      if (lang == null)
      {
        throw IllegalArgumentException("language is null")
      }
      if (pennies == null)
      {
        throw IllegalArgumentException("pennies is null")
      }
      val intPart = amount.toLong()
      var fractPart = 0L
      var result:String
      if (amount.toFloat() == amount.toInt().toFloat())
      {
        result = MoneyToStr(Currency.PER10, lang, pennies).convert(amount.toLong(), 0L)
      }
      else if (java.lang.Double.valueOf(amount * NUM10).toFloat() == java.lang.Double.valueOf(amount * NUM10).toInt().toFloat())
      {
        fractPart = Math.round((amount - intPart) * NUM10)
        result = MoneyToStr(Currency.PER10, lang, pennies).convert(intPart, fractPart)
      }
      else if (java.lang.Double.valueOf(amount * NUM100).toFloat() == java.lang.Double.valueOf(amount * NUM100).toInt().toFloat())
      {
        fractPart = Math.round((amount - intPart) * NUM100)
        result = MoneyToStr(Currency.PER100, lang, pennies).convert(intPart, fractPart)
      }
      else if (java.lang.Double.valueOf(amount * NUM1000).toFloat() == java.lang.Double.valueOf(amount * NUM1000).toInt().toFloat())
      {
        fractPart = Math.round((amount - intPart) * NUM1000)
        result = MoneyToStr(Currency.PER1000, lang, pennies).convert(intPart, fractPart)
      }
      else
      {
        fractPart = Math.round((amount - intPart) * NUM10000)
        result = MoneyToStr(Currency.PER10000, lang, pennies).convert(intPart, fractPart)
      }
      return result
    }
    @JvmStatic fun main(args:Array<String>) {
      var amount = "123.25"
      var language = "ENG"
      var currency = "USD"
      var pennies = "TEXT"
      if (args.size == 0)
      {
        println("Usage: java -jar moneytostr.jar --amount=123.25 --language=rus|ukr|eng --currency=rur|uah|usd --pennies=text|number")
      }
      else
      {
        for (arg in args)
        {
          if (arg.startsWith("--amount="))
          {
            amount = arg.substring("--amount=".length).trim({ it <= ' ' }).replace(",", ".")
          }
          else if (arg.startsWith("--language="))
          {
            language = arg.substring("--language=".length).trim({ it <= ' ' }).toUpperCase()
          }
          else if (arg.startsWith("--currency="))
          {
            currency = arg.substring("--currency=".length).trim({ it <= ' ' }).toUpperCase()
          }
          else if (arg.startsWith("--pennies="))
          {
            pennies = arg.substring("--pennies=".length).trim({ it <= ' ' }).toUpperCase()
          }
        }
        val result = MoneyToStr(Currency.valueOf(currency), Language.valueOf(language), Pennies.valueOf(pennies)).convert(java.lang.Double.valueOf(amount))
        println(result)
      }
    }
  }
}
