package org.hildan.leboncoin.api

import kotlinx.serialization.Serializable

@Serializable
data class ClothingAttributes(
    val type: ClothingType,
    val category: ClothingCategory,
    val size: ClothingSize,
    val condition: ClothingCondition,
    val brand: String,
    val color: String
) {
    fun toLbcAttributes(): Map<String, String> = mapOf(
        "clothing_brand" to brand,
        "clothing_category" to category.lbcName,
        "clothing_color" to color,
        "clothing_condition" to condition.lbcValue.toString(),
        "clothing_st" to size.lbcValue.toString(),
        "clothing_type" to type.lbcValue.toString()
    )
}

enum class ClothingType(val lbcValue: Int) {
    WOMEN(1),
    MATERNITY(2),
    MEN(3),
    CHILDREN(4),
}

enum class ClothingSize(val lbcValue: Int) {
    WOMAN_XXS_32(1),
    WOMAN_XS_34(2),
    WOMAN_S_36(3),
    WOMAN_M_38(4),
    WOMAN_L_40(5),
    WOMAN_XL_42(6),
    WOMAN_XXL_44(7),
    WOMAN_XXXL_46(8),
    WOMAN_XXXXL_48(9),
    WOMAN_XXXXXL_50_PLUS(10),
    XS(1),
    S(2),
    M(3),
    L(4),
    XL(5),
    XXL(6),
    XXXL_PLUS(7),
    CHILD_3_YO(1),
    CHILD_4_YO(2),
    CHILD_5_YO(3),
    CHILD_6_YO(4),
    CHILD_8_YO(5),
    CHILD_10_YO(6),
    CHILD_12_YO(7),
    CHILD_14_YO(8),
    CHILD_16_YO(9),
    CHILD_18_YO(10),
}

enum class ClothingCategory(val lbcName: String) {
    DRESS("robe"),
    COAT_JACKET("manteau"),
    TOP_TSHIRT_POLO("haut"),
    PANTS("pantalon"),
    PULL("pull"),
    JEANS("jean"),
    SHIRT("chemise"),
    SUIT("costume"),
    SHORTS("short"),
    SPORT_DANCE("sport"),
    BATH_BEACH("maillot"),
    LINGERIE("lingerie"),
    UNDERWEAR("sousvetement"),
    DISGUISE("deguisement"),
    WEDDING("mariage"),
    OTHER("autre"),
}

enum class ClothingCondition(val lbcValue: Int) {
    NEW_WITH_LABEL(5),
    NEW_WITHOUT_LABEL(4),
    VERY_GOOD(3),
    GOOD(2),
    SATISFYING(1)
}
