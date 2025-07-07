package com.bardur.domus.api

import com.bardur.domus.model.Property
import org.json.JSONArray
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

interface PropertyApi {
    fun getProperties(): List<Property>
}

object OgnApi : PropertyApi {

    override fun getProperties(): List<Property> {

        val properties: MutableList<Property> = mutableListOf()

        val document = Jsoup.connect("https://ogn.fo/properties").get()

        val section: Element? = document.selectFirst("grid.properties--self.ease-edit.properties")

        if (section != null) {
            val allChildren = section.select("column.properties--property.ease-edit")
            var index = 1
            for (child in allChildren) {

                val statusSection = child?.selectFirst("div.properties--sold")

                if (statusSection != null) {
                    continue
                }
                var latestBid = ""
                var latestBidValidity = ""
                val buildYear = ""

                val imageUrl: String = ("https://ogn.fo/" + child?.selectFirst("img")?.attr("src"))
                var address: String =
                    child.selectFirst("div.properties--address")?.text()?.trim() ?: ""
                val price: String =
                    child?.selectFirst("div.properties--price-suggestion span")?.text()?.trim()
                        ?.replace(".", "")?.replace("kr", "")?.trim() ?: ""
                val city = try {
                    address.split(",")[1].trim().replace(Regex("\\d+"), "").trim()
                } catch (e: Exception) {
                    ""
                }

                address = try {
                    address.split(",")[0].trim()
                } catch (e: Exception) {
                    address
                }

                val offerSpan: Element? = child.selectFirst("div.properties--latest-offer span")

                if (offerSpan != null) {
                    val offer = offerSpan.text().trim()
                    latestBid = offer.replace(".", "").replace("kr", "").trim()
                }

                val dateSpan: Element? = child.selectFirst("div.properties--available-until span")

                if (dateSpan != null) {
                    val date = dateSpan.text().trim()
                    latestBidValidity = date
                }

                val p = Property(
                    address = address,
                    city = city,
                    buildYear = buildYear,
                    listPrice = price,
                    latestBid = latestBid,
                    bidValidUntil = latestBidValidity,
                    image = imageUrl,
                    broker = "Ogn",
                    biddingActive = latestBid.isNotEmpty(),
                    id = "ogn$index"
                )

                index++
                properties.add(p)

            }
        }

        return properties

    }

}

object SkiftApi : PropertyApi {


    override fun getProperties(): List<Property> {

        val properties: MutableList<Property> = mutableListOf()
        val document = Jsoup.connect("https://www.skift.fo/").get()
        val section: Element? = document.selectFirst("div.properties-grid")

        if (section != null) {
            val allChildren = section.children()
            var index = 1
            for (child in allChildren) {

                val statusSection = child?.selectFirst("div.property-card--status")

                if (statusSection != null && statusSection.select("span")
                        .any { it.text().contains("Selt", ignoreCase = true) }
                ) {
                    continue
                }

                var address: String
                var price: String
                val latestBid = ""
                val latestBidValidity = ""
                var buildYear = ""
                var imageUrl: String

                val infoSection = child.selectFirst("div.property-card--text-container")
                val pricesSection = infoSection?.selectFirst("div.property-card--prices-container")
                val addressSection = infoSection?.selectFirst("div.property-card--text-info")
                val imageSection = child?.selectFirst("div.property-card--image-container")
                val sizesSection =  child?.selectFirst("div.property-card--sizes")


                val yearDiv: Element? = sizesSection?.selectFirst(
                    "div.property-card--size:has(img[src=\"https://www.skift.fo/wp-content/themes/kadence-child/images/icon-build-year.svg\"]) > div:nth-of-type(2)"
                )

                if (yearDiv != null) {
                    println("Build year: " + yearDiv.text())
                    buildYear = yearDiv.text()
                } else {
                    println("Build year not found.")
                }

                imageUrl = imageSection?.selectFirst("img")?.attr("src") ?: ""
                address = addressSection?.selectFirst("h5")?.text()?.trim() ?: ""
                price = pricesSection?.selectFirst("h5")?.text()?.trim()?.replace(".", "") ?: ""
                val city = address.split(",")[1].trim()
                address = address.split(",")[0].trim()

                val p = Property(
                    address = address,
                    city = city,
                    buildYear = buildYear,
                    listPrice = price,
                    latestBid = latestBid,
                    bidValidUntil = latestBidValidity,
                    image = imageUrl,
                    broker = "Skift",
                    biddingActive = latestBid.isNotEmpty(),
                    id = "skift$index"
                )

                index++
                properties.add(p)

            }
        }

        return properties

    }

}


object BetriHeimApi : PropertyApi {

    override fun getProperties(): List<Property> {

        val properties: MutableList<Property> = mutableListOf()
        val document = Jsoup.connect("https://www.betriheim.fo/").get()
        val section: Element? = document.selectFirst("section.properties")

        if (section != null) {
            val allChildren = section.children()
            var index = 1
            for (child in allChildren) {

                if (child.selectFirst("div.tag.selt") != null) {
                    continue
                }

                var address = ""
                var price = ""
                var latestBid = ""
                var latestBidValidity = ""
                var buildYear = ""
                var imageUrl = ""

                val infoSection = child.selectFirst("section.info")
                val factsSection = child.selectFirst("section.facts")
                val firstPicture = child.selectFirst("picture")
                if (firstPicture != null) {
                    imageUrl = firstPicture.selectFirst("img")?.attr("src") ?: ""
                }

                if (infoSection != null) {
                    address = infoSection.selectFirst("address.medium")?.text()?.trim() ?: ""
                    address.trim().split("\\s+".toRegex()).last()
                    price =
                        infoSection.selectFirst("div.price")?.text()?.trim()?.replace("Kr. ", "")
                            ?.replace(".", "")
                            ?: ""
                    latestBid = infoSection.selectFirst("div.latest-offer")?.text()?.trim()
                        ?.replace(".", "")?.replace("Seinasta boð: ", "")?.replace("Kr", "")?.trim()
                        ?.replace("Seinasta boð: Kr. ", "") ?: ""
                    latestBidValidity = infoSection.selectFirst("div.valid")?.text()?.trim()
                        ?.replace("Galdandi til ", "") ?: ""

                }

                if (factsSection != null) {
                    buildYear =
                        factsSection.selectFirst("div.date")?.text()?.trim()?.replace("Bygd ", "")
                            ?: ""

                    if(buildYear.contains("/")){
                        buildYear = buildYear.split("/")[0];
                    }

                    if(buildYear.equals("null", ignoreCase = true)){
                        buildYear = ""
                    }
                }

                val city = address.trim().split("\\s+".toRegex()).last()

                val parts = address.trim().split("\\s+".toRegex())
                if (parts.size > 2) {
                    address = parts.dropLast(2).joinToString(" ")
                }

                val p = Property(
                    address = address,
                    city = city,
                    buildYear = buildYear,
                    listPrice = price,
                    latestBid = latestBid,
                    bidValidUntil = latestBidValidity,
                    image = imageUrl,
                    broker = "Betri Heim",
                    biddingActive = latestBid.isNotEmpty(),
                    id = "betri$index"
                )

                index++

                properties.add(p)

            }
        }

        return properties
    }
}

object MeklarinApi : PropertyApi {

    override fun getProperties(): List<Property> {
        val result = extractPropertiesFromHtml("https://www.meklarin.fo/")
        return result
    }

    private fun extractPropertiesFromHtml(url: String): List<Property> {
        val properties = mutableListOf<Property>()
        val document = Jsoup.connect(url).get()
        val scripts = document.getElementsByTag("script")

        for (script in scripts) {
            val html = script.html()
            if (html.contains("var ALL_PROPERTIES = JSON.parse(")) {
                val start = html.indexOf("JSON.parse('") + "JSON.parse('".length
                val end = html.indexOf("')", start)
                val escapedJson = html.substring(start, end)

                val unescaped = escapedJson
                    .replace("\\\\", "\\")
                    .replace("\\\"", "\"")
                    .replace("\\/", "/")

                val jsonArray = JSONArray(unescaped)

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)

                    if (!obj.getBoolean("sold")) {
                        val bid = obj.optString("bid").replace(".", "")
                        val property = Property(
                            address = obj.optString("address"),
                            city = obj.optString("city"),
                            url = obj.optString("permalink"),
                            image = obj.optString("featured_image"),
                            buildYear = obj.optString("build"),
                            latestBid = bid,
                            bidValidUntil = obj.optString("bid_valid_until"),
                            listPrice = obj.optString("price").replace(".", ""),
                            broker = "Meklarin",
                            priceIncomeRatio = 0.0,
                            biddingActive = bid.isNotEmpty(),
                            id = "meklarin$i"
                        )
                        properties.add(property)
                    }
                }
                break
            }
        }
        return properties
    }
}

object SkynApi : PropertyApi {
    override fun getProperties(): List<Property> {
        val doc = Jsoup.connect("https://www.skyn.fo/ognir-til-soelu").get()
        val properties = doc.select("div.col-md-6.col-sm-6.col-xs-12.col-lg-4.ogn:not(.sold)")
        val propertyList: MutableList<Property> = mutableListOf()
        var index = 1
        for (el in properties) {
            val relativeImg = el.select(".ogn_thumb img").attr("src")
            val imageUrl = "https://www.skyn.fo$relativeImg"
            val title = el.select(".ogn_headline").text()
            val address = el.select(".ogn_adress").text()
            var buildYear = el.select(".prop-buildyear")
                .parents()
                .first()
                ?.ownText() ?: ""

            if (buildYear == "−") {
                buildYear = ""
            }

            val latestBid = el.select(".latest-bid .latestoffer").text().replace(".", "")
            val validUntil = el.select(".latest-bid .validto").text().removePrefix("galdandi til ")
            val listPrice = el.select(".latest-bid .listprice").text().replace(".", "")

            val property = Property(
                address = title,
                city = address,
                image = imageUrl,
                buildYear = buildYear,
                listPrice = listPrice,
                latestBid = latestBid,
                bidValidUntil = validUntil,
                broker = "Skyn",
                biddingActive = latestBid.isNotEmpty(),
                id = "skyn$index"
            )
            index++
            propertyList.add(property)
        }
        return propertyList
    }
}