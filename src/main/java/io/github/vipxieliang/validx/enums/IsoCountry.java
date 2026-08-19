/*
 * Copyright 2025-2026 vipxieliang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.vipxieliang.validx.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * ISO 3166-1 国家/地区枚举
 * 每个国家携带三种标准编码（两字母 alpha-2、三字母 alpha-3、三位数字 numeric）
 * 以及英文全称（English Name）与中文名（Chinese Name）
 * </p>
 *
 * <p>
 * 三种编码一一对应、由同一枚举常量承载，从根本上避免"多张表数据不同步"的问题。
 * 典型应用场景：
 * <ul>
 *   <li>SWIFT/BIC 银行代码中的国家代码（alpha-2）</li>
 *   <li>国际航空、联合国文件中的国家代码（alpha-3）</li>
 *   <li>外国人永久居留身份证（五星卡）号码第 4~6 位国籍国代码（numeric）</li>
 * </ul>
 * </p>
 *
 * @author vipxieliang
 * @since 2026/08/18
 */
public enum IsoCountry {

    /** 阿富汗 Afghanistan */
    AF("AF", "AFG", "004", "Afghanistan", "阿富汗"),
    /** 奥兰群岛 Åland Islands */
    AX("AX", "ALA", "248", "Åland Islands", "奥兰群岛"),
    /** 阿尔巴尼亚 Albania */
    AL("AL", "ALB", "008", "Albania", "阿尔巴尼亚"),
    /** 阿尔及利亚 Algeria */
    DZ("DZ", "DZA", "012", "Algeria", "阿尔及利亚"),
    /** 美属萨摩亚 American Samoa */
    AS("AS", "ASM", "016", "American Samoa", "美属萨摩亚"),
    /** 安道尔 Andorra */
    AD("AD", "AND", "020", "Andorra", "安道尔"),
    /** 安哥拉 Angola */
    AO("AO", "AGO", "024", "Angola", "安哥拉"),
    /** 安圭拉 Anguilla */
    AI("AI", "AIA", "660", "Anguilla", "安圭拉"),
    /** 南极洲 Antarctica */
    AQ("AQ", "ATA", "010", "Antarctica", "南极洲"),
    /** 安提瓜和巴布达 Antigua and Barbuda */
    AG("AG", "ATG", "028", "Antigua and Barbuda", "安提瓜和巴布达"),
    /** 阿根廷 Argentina */
    AR("AR", "ARG", "032", "Argentina", "阿根廷"),
    /** 亚美尼亚 Armenia */
    AM("AM", "ARM", "051", "Armenia", "亚美尼亚"),
    /** 阿鲁巴 Aruba */
    AW("AW", "ABW", "533", "Aruba", "阿鲁巴"),
    /** 澳大利亚 Australia */
    AU("AU", "AUS", "036", "Australia", "澳大利亚"),
    /** 奥地利 Austria */
    AT("AT", "AUT", "040", "Austria", "奥地利"),
    /** 阿塞拜疆 Azerbaijan */
    AZ("AZ", "AZE", "031", "Azerbaijan", "阿塞拜疆"),
    /** 巴哈马 Bahamas */
    BS("BS", "BHS", "044", "Bahamas", "巴哈马"),
    /** 巴林 Bahrain */
    BH("BH", "BHR", "048", "Bahrain", "巴林"),
    /** 孟加拉国 Bangladesh */
    BD("BD", "BGD", "050", "Bangladesh", "孟加拉国"),
    /** 巴巴多斯 Barbados */
    BB("BB", "BRB", "052", "Barbados", "巴巴多斯"),
    /** 白俄罗斯 Belarus */
    BY("BY", "BLR", "112", "Belarus", "白俄罗斯"),
    /** 比利时 Belgium */
    BE("BE", "BEL", "056", "Belgium", "比利时"),
    /** 伯利兹 Belize */
    BZ("BZ", "BLZ", "084", "Belize", "伯利兹"),
    /** 贝宁 Benin */
    BJ("BJ", "BEN", "204", "Benin", "贝宁"),
    /** 百慕大 Bermuda */
    BM("BM", "BMU", "060", "Bermuda", "百慕大"),
    /** 不丹 Bhutan */
    BT("BT", "BTN", "064", "Bhutan", "不丹"),
    /** 玻利维亚 Bolivia */
    BO("BO", "BOL", "068", "Bolivia", "玻利维亚"),
    /** 荷兰加勒比区 Bonaire, Sint Eustatius and Saba */
    BQ("BQ", "BES", "535", "Bonaire, Sint Eustatius and Saba", "荷兰加勒比区"),
    /** 波斯尼亚和黑塞哥维那 Bosnia and Herzegovina */
    BA("BA", "BIH", "070", "Bosnia and Herzegovina", "波斯尼亚和黑塞哥维那"),
    /** 博茨瓦纳 Botswana */
    BW("BW", "BWA", "072", "Botswana", "博茨瓦纳"),
    /** 布韦岛 Bouvet Island */
    BV("BV", "BVT", "074", "Bouvet Island", "布韦岛"),
    /** 巴西 Brazil */
    BR("BR", "BRA", "076", "Brazil", "巴西"),
    /** 英属印度洋领地 British Indian Ocean Territory */
    IO("IO", "IOT", "086", "British Indian Ocean Territory", "英属印度洋领地"),
    /** 文莱 Brunei Darussalam */
    BN("BN", "BRN", "096", "Brunei Darussalam", "文莱"),
    /** 保加利亚 Bulgaria */
    BG("BG", "BGR", "100", "Bulgaria", "保加利亚"),
    /** 布基纳法索 Burkina Faso */
    BF("BF", "BFA", "854", "Burkina Faso", "布基纳法索"),
    /** 布隆迪 Burundi */
    BI("BI", "BDI", "108", "Burundi", "布隆迪"),
    /** 佛得角 Cabo Verde */
    CV("CV", "CPV", "132", "Cabo Verde", "佛得角"),
    /** 柬埔寨 Cambodia */
    KH("KH", "KHM", "116", "Cambodia", "柬埔寨"),
    /** 喀麦隆 Cameroon */
    CM("CM", "CMR", "120", "Cameroon", "喀麦隆"),
    /** 加拿大 Canada */
    CA("CA", "CAN", "124", "Canada", "加拿大"),
    /** 开曼群岛 Cayman Islands */
    KY("KY", "CYM", "136", "Cayman Islands", "开曼群岛"),
    /** 中非共和国 Central African Republic */
    CF("CF", "CAF", "140", "Central African Republic", "中非共和国"),
    /** 乍得 Chad */
    TD("TD", "TCD", "148", "Chad", "乍得"),
    /** 智利 Chile */
    CL("CL", "CHL", "152", "Chile", "智利"),
    /** 中国 China */
    CN("CN", "CHN", "156", "China", "中国"),
    /** 圣诞岛 Christmas Island */
    CX("CX", "CXR", "162", "Christmas Island", "圣诞岛"),
    /** 科科斯群岛 Cocos (Keeling) Islands */
    CC("CC", "CCK", "166", "Cocos (Keeling) Islands", "科科斯群岛"),
    /** 哥伦比亚 Colombia */
    CO("CO", "COL", "170", "Colombia", "哥伦比亚"),
    /** 科摩罗 Comoros */
    KM("KM", "COM", "174", "Comoros", "科摩罗"),
    /** 刚果（布） Congo */
    CG("CG", "COG", "178", "Congo", "刚果（布）"),
    /** 刚果（金） Congo, Democratic Republic of the */
    CD("CD", "COD", "180", "Congo, Democratic Republic of the", "刚果（金）"),
    /** 库克群岛 Cook Islands */
    CK("CK", "COK", "184", "Cook Islands", "库克群岛"),
    /** 哥斯达黎加 Costa Rica */
    CR("CR", "CRI", "188", "Costa Rica", "哥斯达黎加"),
    /** 科特迪瓦 Côte d'Ivoire */
    CI("CI", "CIV", "384", "Côte d'Ivoire", "科特迪瓦"),
    /** 克罗地亚 Croatia */
    HR("HR", "HRV", "191", "Croatia", "克罗地亚"),
    /** 古巴 Cuba */
    CU("CU", "CUB", "192", "Cuba", "古巴"),
    /** 库拉索 Curaçao */
    CW("CW", "CUW", "531", "Curaçao", "库拉索"),
    /** 塞浦路斯 Cyprus */
    CY("CY", "CYP", "196", "Cyprus", "塞浦路斯"),
    /** 捷克 Czechia */
    CZ("CZ", "CZE", "203", "Czechia", "捷克"),
    /** 丹麦 Denmark */
    DK("DK", "DNK", "208", "Denmark", "丹麦"),
    /** 吉布提 Djibouti */
    DJ("DJ", "DJI", "262", "Djibouti", "吉布提"),
    /** 多米尼克 Dominica */
    DM("DM", "DMA", "212", "Dominica", "多米尼克"),
    /** 多米尼加 Dominican Republic */
    DO("DO", "DOM", "214", "Dominican Republic", "多米尼加"),
    /** 厄瓜多尔 Ecuador */
    EC("EC", "ECU", "218", "Ecuador", "厄瓜多尔"),
    /** 埃及 Egypt */
    EG("EG", "EGY", "818", "Egypt", "埃及"),
    /** 萨尔瓦多 El Salvador */
    SV("SV", "SLV", "222", "El Salvador", "萨尔瓦多"),
    /** 赤道几内亚 Equatorial Guinea */
    GQ("GQ", "GNQ", "226", "Equatorial Guinea", "赤道几内亚"),
    /** 厄立特里亚 Eritrea */
    ER("ER", "ERI", "232", "Eritrea", "厄立特里亚"),
    /** 爱沙尼亚 Estonia */
    EE("EE", "EST", "233", "Estonia", "爱沙尼亚"),
    /** 斯威士兰 Eswatini */
    SZ("SZ", "SWZ", "748", "Eswatini", "斯威士兰"),
    /** 埃塞俄比亚 Ethiopia */
    ET("ET", "ETH", "231", "Ethiopia", "埃塞俄比亚"),
    /** 福克兰群岛 Falkland Islands */
    FK("FK", "FLK", "238", "Falkland Islands", "福克兰群岛"),
    /** 法罗群岛 Faroe Islands */
    FO("FO", "FRO", "234", "Faroe Islands", "法罗群岛"),
    /** 斐济 Fiji */
    FJ("FJ", "FJI", "242", "Fiji", "斐济"),
    /** 芬兰 Finland */
    FI("FI", "FIN", "246", "Finland", "芬兰"),
    /** 法国 France */
    FR("FR", "FRA", "250", "France", "法国"),
    /** 法属圭亚那 French Guiana */
    GF("GF", "GUF", "254", "French Guiana", "法属圭亚那"),
    /** 法属波利尼西亚 French Polynesia */
    PF("PF", "PYF", "258", "French Polynesia", "法属波利尼西亚"),
    /** 法属南部领地 French Southern Territories */
    TF("TF", "ATF", "260", "French Southern Territories", "法属南部领地"),
    /** 加蓬 Gabon */
    GA("GA", "GAB", "266", "Gabon", "加蓬"),
    /** 冈比亚 Gambia */
    GM("GM", "GMB", "270", "Gambia", "冈比亚"),
    /** 格鲁吉亚 Georgia */
    GE("GE", "GEO", "268", "Georgia", "格鲁吉亚"),
    /** 德国 Germany */
    DE("DE", "DEU", "276", "Germany", "德国"),
    /** 加纳 Ghana */
    GH("GH", "GHA", "288", "Ghana", "加纳"),
    /** 直布罗陀 Gibraltar */
    GI("GI", "GIB", "292", "Gibraltar", "直布罗陀"),
    /** 希腊 Greece */
    GR("GR", "GRC", "300", "Greece", "希腊"),
    /** 格陵兰 Greenland */
    GL("GL", "GRL", "304", "Greenland", "格陵兰"),
    /** 格林纳达 Grenada */
    GD("GD", "GRD", "308", "Grenada", "格林纳达"),
    /** 瓜德罗普 Guadeloupe */
    GP("GP", "GLP", "312", "Guadeloupe", "瓜德罗普"),
    /** 关岛 Guam */
    GU("GU", "GUM", "316", "Guam", "关岛"),
    /** 危地马拉 Guatemala */
    GT("GT", "GTM", "320", "Guatemala", "危地马拉"),
    /** 根西 Guernsey */
    GG("GG", "GGY", "831", "Guernsey", "根西"),
    /** 几内亚 Guinea */
    GN("GN", "GIN", "324", "Guinea", "几内亚"),
    /** 几内亚比绍 Guinea-Bissau */
    GW("GW", "GNB", "624", "Guinea-Bissau", "几内亚比绍"),
    /** 圭亚那 Guyana */
    GY("GY", "GUY", "328", "Guyana", "圭亚那"),
    /** 海地 Haiti */
    HT("HT", "HTI", "332", "Haiti", "海地"),
    /** 赫德岛和麦克唐纳群岛 Heard Island and McDonald Islands */
    HM("HM", "HMD", "334", "Heard Island and McDonald Islands", "赫德岛和麦克唐纳群岛"),
    /** 梵蒂冈 Holy See */
    VA("VA", "VAT", "336", "Holy See", "梵蒂冈"),
    /** 洪都拉斯 Honduras */
    HN("HN", "HND", "340", "Honduras", "洪都拉斯"),
    /** 香港 Hong Kong */
    HK("HK", "HKG", "344", "Hong Kong", "香港"),
    /** 匈牙利 Hungary */
    HU("HU", "HUN", "348", "Hungary", "匈牙利"),
    /** 冰岛 Iceland */
    IS("IS", "ISL", "352", "Iceland", "冰岛"),
    /** 印度 India */
    IN("IN", "IND", "356", "India", "印度"),
    /** 印度尼西亚 Indonesia */
    ID("ID", "IDN", "360", "Indonesia", "印度尼西亚"),
    /** 伊朗 Iran */
    IR("IR", "IRN", "364", "Iran", "伊朗"),
    /** 伊拉克 Iraq */
    IQ("IQ", "IRQ", "368", "Iraq", "伊拉克"),
    /** 爱尔兰 Ireland */
    IE("IE", "IRL", "372", "Ireland", "爱尔兰"),
    /** 马恩岛 Isle of Man */
    IM("IM", "IMN", "833", "Isle of Man", "马恩岛"),
    /** 以色列 Israel */
    IL("IL", "ISR", "376", "Israel", "以色列"),
    /** 意大利 Italy */
    IT("IT", "ITA", "380", "Italy", "意大利"),
    /** 牙买加 Jamaica */
    JM("JM", "JAM", "388", "Jamaica", "牙买加"),
    /** 日本 Japan */
    JP("JP", "JPN", "392", "Japan", "日本"),
    /** 泽西 Jersey */
    JE("JE", "JEY", "832", "Jersey", "泽西"),
    /** 约旦 Jordan */
    JO("JO", "JOR", "400", "Jordan", "约旦"),
    /** 哈萨克斯坦 Kazakhstan */
    KZ("KZ", "KAZ", "398", "Kazakhstan", "哈萨克斯坦"),
    /** 肯尼亚 Kenya */
    KE("KE", "KEN", "404", "Kenya", "肯尼亚"),
    /** 基里巴斯 Kiribati */
    KI("KI", "KIR", "296", "Kiribati", "基里巴斯"),
    /** 朝鲜 Korea, Democratic People's Republic of */
    KP("KP", "PRK", "408", "Korea, Democratic People's Republic of", "朝鲜"),
    /** 韩国 Korea, Republic of */
    KR("KR", "KOR", "410", "Korea, Republic of", "韩国"),
    /** 科威特 Kuwait */
    KW("KW", "KWT", "414", "Kuwait", "科威特"),
    /** 吉尔吉斯斯坦 Kyrgyzstan */
    KG("KG", "KGZ", "417", "Kyrgyzstan", "吉尔吉斯斯坦"),
    /** 老挝 Lao People's Democratic Republic */
    LA("LA", "LAO", "418", "Lao People's Democratic Republic", "老挝"),
    /** 拉脱维亚 Latvia */
    LV("LV", "LVA", "428", "Latvia", "拉脱维亚"),
    /** 黎巴嫩 Lebanon */
    LB("LB", "LBN", "422", "Lebanon", "黎巴嫩"),
    /** 莱索托 Lesotho */
    LS("LS", "LSO", "426", "Lesotho", "莱索托"),
    /** 利比里亚 Liberia */
    LR("LR", "LBR", "430", "Liberia", "利比里亚"),
    /** 利比亚 Libya */
    LY("LY", "LBY", "434", "Libya", "利比亚"),
    /** 列支敦士登 Liechtenstein */
    LI("LI", "LIE", "438", "Liechtenstein", "列支敦士登"),
    /** 立陶宛 Lithuania */
    LT("LT", "LTU", "440", "Lithuania", "立陶宛"),
    /** 卢森堡 Luxembourg */
    LU("LU", "LUX", "442", "Luxembourg", "卢森堡"),
    /** 澳门 Macao */
    MO("MO", "MAC", "446", "Macao", "澳门"),
    /** 马达加斯加 Madagascar */
    MG("MG", "MDG", "450", "Madagascar", "马达加斯加"),
    /** 马拉维 Malawi */
    MW("MW", "MWI", "454", "Malawi", "马拉维"),
    /** 马来西亚 Malaysia */
    MY("MY", "MYS", "458", "Malaysia", "马来西亚"),
    /** 马尔代夫 Maldives */
    MV("MV", "MDV", "462", "Maldives", "马尔代夫"),
    /** 马里 Mali */
    ML("ML", "MLI", "466", "Mali", "马里"),
    /** 马耳他 Malta */
    MT("MT", "MLT", "470", "Malta", "马耳他"),
    /** 马绍尔群岛 Marshall Islands */
    MH("MH", "MHL", "584", "Marshall Islands", "马绍尔群岛"),
    /** 马提尼克 Martinique */
    MQ("MQ", "MTQ", "474", "Martinique", "马提尼克"),
    /** 毛里塔尼亚 Mauritania */
    MR("MR", "MRT", "478", "Mauritania", "毛里塔尼亚"),
    /** 毛里求斯 Mauritius */
    MU("MU", "MUS", "480", "Mauritius", "毛里求斯"),
    /** 马约特 Mayotte */
    YT("YT", "MYT", "175", "Mayotte", "马约特"),
    /** 墨西哥 Mexico */
    MX("MX", "MEX", "484", "Mexico", "墨西哥"),
    /** 密克罗尼西亚 Micronesia */
    FM("FM", "FSM", "583", "Micronesia", "密克罗尼西亚"),
    /** 摩尔多瓦 Moldova */
    MD("MD", "MDA", "498", "Moldova", "摩尔多瓦"),
    /** 摩纳哥 Monaco */
    MC("MC", "MCO", "492", "Monaco", "摩纳哥"),
    /** 蒙古 Mongolia */
    MN("MN", "MNG", "496", "Mongolia", "蒙古"),
    /** 黑山 Montenegro */
    ME("ME", "MNE", "499", "Montenegro", "黑山"),
    /** 蒙特塞拉特 Montserrat */
    MS("MS", "MSR", "500", "Montserrat", "蒙特塞拉特"),
    /** 摩洛哥 Morocco */
    MA("MA", "MAR", "504", "Morocco", "摩洛哥"),
    /** 莫桑比克 Mozambique */
    MZ("MZ", "MOZ", "508", "Mozambique", "莫桑比克"),
    /** 缅甸 Myanmar */
    MM("MM", "MMR", "104", "Myanmar", "缅甸"),
    /** 纳米比亚 Namibia */
    NA("NA", "NAM", "516", "Namibia", "纳米比亚"),
    /** 瑙鲁 Nauru */
    NR("NR", "NRU", "520", "Nauru", "瑙鲁"),
    /** 尼泊尔 Nepal */
    NP("NP", "NPL", "524", "Nepal", "尼泊尔"),
    /** 荷兰 Netherlands */
    NL("NL", "NLD", "528", "Netherlands", "荷兰"),
    /** 新喀里多尼亚 New Caledonia */
    NC("NC", "NCL", "540", "New Caledonia", "新喀里多尼亚"),
    /** 新西兰 New Zealand */
    NZ("NZ", "NZL", "554", "New Zealand", "新西兰"),
    /** 尼加拉瓜 Nicaragua */
    NI("NI", "NIC", "558", "Nicaragua", "尼加拉瓜"),
    /** 尼日尔 Niger */
    NE("NE", "NER", "562", "Niger", "尼日尔"),
    /** 尼日利亚 Nigeria */
    NG("NG", "NGA", "566", "Nigeria", "尼日利亚"),
    /** 纽埃 Niue */
    NU("NU", "NIU", "570", "Niue", "纽埃"),
    /** 诺福克岛 Norfolk Island */
    NF("NF", "NFK", "574", "Norfolk Island", "诺福克岛"),
    /** 北马其顿 North Macedonia */
    MK("MK", "MKD", "807", "North Macedonia", "北马其顿"),
    /** 北马里亚纳群岛 Northern Mariana Islands */
    MP("MP", "MNP", "580", "Northern Mariana Islands", "北马里亚纳群岛"),
    /** 挪威 Norway */
    NO("NO", "NOR", "578", "Norway", "挪威"),
    /** 阿曼 Oman */
    OM("OM", "OMN", "512", "Oman", "阿曼"),
    /** 巴基斯坦 Pakistan */
    PK("PK", "PAK", "586", "Pakistan", "巴基斯坦"),
    /** 帕劳 Palau */
    PW("PW", "PLW", "585", "Palau", "帕劳"),
    /** 巴勒斯坦 Palestine */
    PS("PS", "PSE", "275", "Palestine", "巴勒斯坦"),
    /** 巴拿马 Panama */
    PA("PA", "PAN", "591", "Panama", "巴拿马"),
    /** 巴布亚新几内亚 Papua New Guinea */
    PG("PG", "PNG", "598", "Papua New Guinea", "巴布亚新几内亚"),
    /** 巴拉圭 Paraguay */
    PY("PY", "PRY", "600", "Paraguay", "巴拉圭"),
    /** 秘鲁 Peru */
    PE("PE", "PER", "604", "Peru", "秘鲁"),
    /** 菲律宾 Philippines */
    PH("PH", "PHL", "608", "Philippines", "菲律宾"),
    /** 皮特凯恩群岛 Pitcairn */
    PN("PN", "PCN", "612", "Pitcairn", "皮特凯恩群岛"),
    /** 波兰 Poland */
    PL("PL", "POL", "616", "Poland", "波兰"),
    /** 葡萄牙 Portugal */
    PT("PT", "PRT", "620", "Portugal", "葡萄牙"),
    /** 波多黎各 Puerto Rico */
    PR("PR", "PRI", "630", "Puerto Rico", "波多黎各"),
    /** 卡塔尔 Qatar */
    QA("QA", "QAT", "634", "Qatar", "卡塔尔"),
    /** 留尼汪 Réunion */
    RE("RE", "REU", "638", "Réunion", "留尼汪"),
    /** 罗马尼亚 Romania */
    RO("RO", "ROU", "642", "Romania", "罗马尼亚"),
    /** 俄罗斯 Russian Federation */
    RU("RU", "RUS", "643", "Russian Federation", "俄罗斯"),
    /** 卢旺达 Rwanda */
    RW("RW", "RWA", "646", "Rwanda", "卢旺达"),
    /** 圣巴泰勒米 Saint Barthélemy */
    BL("BL", "BLM", "652", "Saint Barthélemy", "圣巴泰勒米"),
    /** 圣赫勒拿 Saint Helena, Ascension and Tristan da Cunha */
    SH("SH", "SHN", "654", "Saint Helena, Ascension and Tristan da Cunha", "圣赫勒拿"),
    /** 圣基茨和尼维斯 Saint Kitts and Nevis */
    KN("KN", "KNA", "659", "Saint Kitts and Nevis", "圣基茨和尼维斯"),
    /** 圣卢西亚 Saint Lucia */
    LC("LC", "LCA", "662", "Saint Lucia", "圣卢西亚"),
    /** 圣马丁（法属） Saint Martin (French part) */
    MF("MF", "MAF", "663", "Saint Martin (French part)", "圣马丁（法属）"),
    /** 圣皮埃尔和密克隆 Saint Pierre and Miquelon */
    PM("PM", "SPM", "666", "Saint Pierre and Miquelon", "圣皮埃尔和密克隆"),
    /** 圣文森特和格林纳丁斯 Saint Vincent and the Grenadines */
    VC("VC", "VCT", "670", "Saint Vincent and the Grenadines", "圣文森特和格林纳丁斯"),
    /** 萨摩亚 Samoa */
    WS("WS", "WSM", "882", "Samoa", "萨摩亚"),
    /** 圣马力诺 San Marino */
    SM("SM", "SMR", "674", "San Marino", "圣马力诺"),
    /** 圣多美和普林西比 Sao Tome and Principe */
    ST("ST", "STP", "678", "Sao Tome and Principe", "圣多美和普林西比"),
    /** 沙特阿拉伯 Saudi Arabia */
    SA("SA", "SAU", "682", "Saudi Arabia", "沙特阿拉伯"),
    /** 塞内加尔 Senegal */
    SN("SN", "SEN", "686", "Senegal", "塞内加尔"),
    /** 塞尔维亚 Serbia */
    RS("RS", "SRB", "688", "Serbia", "塞尔维亚"),
    /** 塞舌尔 Seychelles */
    SC("SC", "SYC", "690", "Seychelles", "塞舌尔"),
    /** 塞拉利昂 Sierra Leone */
    SL("SL", "SLE", "694", "Sierra Leone", "塞拉利昂"),
    /** 新加坡 Singapore */
    SG("SG", "SGP", "702", "Singapore", "新加坡"),
    /** 圣马丁（荷属） Sint Maarten (Dutch part) */
    SX("SX", "SXM", "534", "Sint Maarten (Dutch part)", "圣马丁（荷属）"),
    /** 斯洛伐克 Slovakia */
    SK("SK", "SVK", "703", "Slovakia", "斯洛伐克"),
    /** 斯洛文尼亚 Slovenia */
    SI("SI", "SVN", "705", "Slovenia", "斯洛文尼亚"),
    /** 所罗门群岛 Solomon Islands */
    SB("SB", "SLB", "090", "Solomon Islands", "所罗门群岛"),
    /** 索马里 Somalia */
    SO("SO", "SOM", "706", "Somalia", "索马里"),
    /** 南非 South Africa */
    ZA("ZA", "ZAF", "710", "South Africa", "南非"),
    /** 南乔治亚和南桑威奇群岛 South Georgia and the South Sandwich Islands */
    GS("GS", "SGS", "239", "South Georgia and the South Sandwich Islands", "南乔治亚和南桑威奇群岛"),
    /** 南苏丹 South Sudan */
    SS("SS", "SSD", "728", "South Sudan", "南苏丹"),
    /** 西班牙 Spain */
    ES("ES", "ESP", "724", "Spain", "西班牙"),
    /** 斯里兰卡 Sri Lanka */
    LK("LK", "LKA", "144", "Sri Lanka", "斯里兰卡"),
    /** 苏丹 Sudan */
    SD("SD", "SDN", "729", "Sudan", "苏丹"),
    /** 苏里南 Suriname */
    SR("SR", "SUR", "740", "Suriname", "苏里南"),
    /** 斯瓦尔巴和扬马延 Svalbard and Jan Mayen */
    SJ("SJ", "SJM", "744", "Svalbard and Jan Mayen", "斯瓦尔巴和扬马延"),
    /** 瑞典 Sweden */
    SE("SE", "SWE", "752", "Sweden", "瑞典"),
    /** 瑞士 Switzerland */
    CH("CH", "CHE", "756", "Switzerland", "瑞士"),
    /** 叙利亚 Syrian Arab Republic */
    SY("SY", "SYR", "760", "Syrian Arab Republic", "叙利亚"),
    /** 台湾 Taiwan */
    TW("TW", "TWN", "158", "Taiwan", "台湾"),
    /** 塔吉克斯坦 Tajikistan */
    TJ("TJ", "TJK", "762", "Tajikistan", "塔吉克斯坦"),
    /** 坦桑尼亚 Tanzania, United Republic of */
    TZ("TZ", "TZA", "834", "Tanzania, United Republic of", "坦桑尼亚"),
    /** 泰国 Thailand */
    TH("TH", "THA", "764", "Thailand", "泰国"),
    /** 东帝汶 Timor-Leste */
    TL("TL", "TLS", "626", "Timor-Leste", "东帝汶"),
    /** 多哥 Togo */
    TG("TG", "TGO", "768", "Togo", "多哥"),
    /** 托克劳 Tokelau */
    TK("TK", "TKL", "772", "Tokelau", "托克劳"),
    /** 汤加 Tonga */
    TO("TO", "TON", "776", "Tonga", "汤加"),
    /** 特立尼达和多巴哥 Trinidad and Tobago */
    TT("TT", "TTO", "780", "Trinidad and Tobago", "特立尼达和多巴哥"),
    /** 突尼斯 Tunisia */
    TN("TN", "TUN", "788", "Tunisia", "突尼斯"),
    /** 土耳其 Türkiye */
    TR("TR", "TUR", "792", "Türkiye", "土耳其"),
    /** 土库曼斯坦 Turkmenistan */
    TM("TM", "TKM", "795", "Turkmenistan", "土库曼斯坦"),
    /** 特克斯和凯科斯群岛 Turks and Caicos Islands */
    TC("TC", "TCA", "796", "Turks and Caicos Islands", "特克斯和凯科斯群岛"),
    /** 图瓦卢 Tuvalu */
    TV("TV", "TUV", "798", "Tuvalu", "图瓦卢"),
    /** 乌干达 Uganda */
    UG("UG", "UGA", "800", "Uganda", "乌干达"),
    /** 乌克兰 Ukraine */
    UA("UA", "UKR", "804", "Ukraine", "乌克兰"),
    /** 阿联酋 United Arab Emirates */
    AE("AE", "ARE", "784", "United Arab Emirates", "阿联酋"),
    /** 英国 United Kingdom */
    GB("GB", "GBR", "826", "United Kingdom", "英国"),
    /** 美国 United States */
    US("US", "USA", "840", "United States", "美国"),
    /** 美国本土外小岛屿 United States Minor Outlying Islands */
    UM("UM", "UMI", "581", "United States Minor Outlying Islands", "美国本土外小岛屿"),
    /** 乌拉圭 Uruguay */
    UY("UY", "URY", "858", "Uruguay", "乌拉圭"),
    /** 乌兹别克斯坦 Uzbekistan */
    UZ("UZ", "UZB", "860", "Uzbekistan", "乌兹别克斯坦"),
    /** 瓦努阿图 Vanuatu */
    VU("VU", "VUT", "548", "Vanuatu", "瓦努阿图"),
    /** 委内瑞拉 Venezuela */
    VE("VE", "VEN", "862", "Venezuela", "委内瑞拉"),
    /** 越南 Viet Nam */
    VN("VN", "VNM", "704", "Viet Nam", "越南"),
    /** 英属维尔京群岛 Virgin Islands (British) */
    VG("VG", "VGB", "092", "Virgin Islands (British)", "英属维尔京群岛"),
    /** 美属维尔京群岛 Virgin Islands (U.S.) */
    VI("VI", "VIR", "850", "Virgin Islands (U.S.)", "美属维尔京群岛"),
    /** 瓦利斯和富图纳 Wallis and Futuna */
    WF("WF", "WLF", "876", "Wallis and Futuna", "瓦利斯和富图纳"),
    /** 西撒哈拉 Western Sahara */
    EH("EH", "ESH", "732", "Western Sahara", "西撒哈拉"),
    /** 也门 Yemen */
    YE("YE", "YEM", "887", "Yemen", "也门"),
    /** 赞比亚 Zambia */
    ZM("ZM", "ZMB", "894", "Zambia", "赞比亚"),
    /** 津巴布韦 Zimbabwe */
    ZW("ZW", "ZWE", "716", "Zimbabwe", "津巴布韦");

    private final String alpha2;
    private final String alpha3;
    private final String numeric;
    private final String englishName;
    private final String chineseName;

    // 三张查找缓存表：O(1) 查询
    private static final Map<String, IsoCountry> BY_ALPHA2 = new HashMap<>();
    private static final Map<String, IsoCountry> BY_ALPHA3 = new HashMap<>();
    private static final Map<String, IsoCountry> BY_NUMERIC = new HashMap<>();

    static {
        for (IsoCountry country : values()) {
            BY_ALPHA2.put(country.alpha2, country);
            BY_ALPHA3.put(country.alpha3, country);
            BY_NUMERIC.put(country.numeric, country);
        }
    }

    IsoCountry(String alpha2, String alpha3, String numeric, String englishName, String chineseName) {
        this.alpha2 = alpha2;
        this.alpha3 = alpha3;
        this.numeric = numeric;
        this.englishName = englishName;
        this.chineseName = chineseName;
    }

    /**
     * 获取两字母代码（ISO 3166-1 alpha-2）
     * @return 两字母代码，如 CA、CN、US
     */
    public String getAlpha2() {
        return alpha2;
    }

    /**
     * 获取三字母代码（ISO 3166-1 alpha-3）
     * @return 三字母代码，如 CAN、CHN、USA
     */
    public String getAlpha3() {
        return alpha3;
    }

    /**
     * 获取三位数字代码（ISO 3166-1 numeric）
     * @return 三位数字代码，如 124、156、840
     */
    public String getNumeric() {
        return numeric;
    }

    /**
     * 获取英文全称（ISO 3166-1 English short name）
     * @return 英文全称，如 "Canada"、"China"、"United States"
     */
    public String getEnglishName() {
        return englishName;
    }

    /**
     * 获取中文名
     * @return 中文名，如 "加拿大"、"中国"、"美国"
     */
    public String getChineseName() {
        return chineseName;
    }

    /**
     * 根据两字母代码查找国家（大小写不敏感）
     * @param code 两字母代码，如 "CA"、"ca"
     * @return 匹配的国家，未找到返回 Optional.empty()
     */
    public static Optional<IsoCountry> fromAlpha2(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(BY_ALPHA2.get(code.toUpperCase()));
    }

    /**
     * 根据三字母代码查找国家（大小写不敏感）
     * @param code 三字母代码，如 "CAN"、"can"
     * @return 匹配的国家，未找到返回 Optional.empty()
     */
    public static Optional<IsoCountry> fromAlpha3(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(BY_ALPHA3.get(code.toUpperCase()));
    }

    /**
     * 根据三位数字代码查找国家
     * @param code 三位数字代码，如 "124"、"156"
     * @return 匹配的国家，未找到返回 Optional.empty()
     */
    public static Optional<IsoCountry> fromNumeric(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(BY_NUMERIC.get(code));
    }
}
