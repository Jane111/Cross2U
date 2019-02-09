package com.cross2u.user.model;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * Generated by JFinal, do not modify this file.
 * <pre>
 * Example:
 * public void configPlugin(Plugins me) {
 *     ActiveRecordPlugin arp = new ActiveRecordPlugin(...);
 *     _MappingKit.mapping(arp);
 *     me.add(arp);
 * }
 * </pre>
 */
public class _MappingKit {

	public static void mapping(ActiveRecordPlugin arp) {
		arp.addMapping("abchart", "abchId", Abchart.class);
		arp.addMapping("abevalreport", "aerId", Abevalreport.class);
		arp.addMapping("abnormalgoodsinfo", "agiId", Abnormalgoodsinfo.class);
		arp.addMapping("abnormalminfo", "amiId", Abnormalminfo.class);
		arp.addMapping("administrator", "aId", Administrator.class);
		arp.addMapping("adminkeyword", "akId", Adminkeyword.class);
		arp.addMapping("adminreply", "arId", Adminreply.class);
		arp.addMapping("amchart", "amchId", Amchart.class);
		arp.addMapping("answer", "anId", Answer.class);
		arp.addMapping("atrribute", "atId", Atrribute.class);
		arp.addMapping("attributeoption", "aoId", Attributeoption.class);
		arp.addMapping("bevalreply", "berId", Bevalreply.class);
		arp.addMapping("browserecord", "brId", Browserecord.class);
		arp.addMapping("business", "bId", Business.class);
		arp.addMapping("businesssearchrecord", "bsrId", Businesssearchrecord.class);
		arp.addMapping("category", "ctId", Category.class);
		arp.addMapping("categoryattribute", "caId", Categoryattribute.class);
		arp.addMapping("categoryformat", "cfId", Categoryformat.class);
		arp.addMapping("collect", "cId", Collect.class);
		arp.addMapping("cooperation", "copId", Cooperation.class);
		arp.addMapping("delivertemp", "dtId", Delivertemp.class);
		arp.addMapping("drawbackinfo", "diId", Drawbackinfo.class);
		arp.addMapping("drawbackreasons", "drId", Drawbackreasons.class);
		arp.addMapping("evaluatebonus", "ebId", Evaluatebonus.class);
		arp.addMapping("evalware", "ewId", Evalware.class);
		arp.addMapping("format", "fId", Format.class);
		arp.addMapping("formatoption", "foId", Formatoption.class);
		arp.addMapping("indent", "inId", Indent.class);
		arp.addMapping("mainmanufacturer", "mmId", Mainmanufacturer.class);
		arp.addMapping("manufacturer", "mId", Manufacturer.class);
		arp.addMapping("manukeyword", "mkId", Manukeyword.class);
		arp.addMapping("mbchat", "mbchId", Mbchat.class);
		arp.addMapping("mevalreply", "mepId", Mevalreply.class);
		arp.addMapping("outindent", "outId", Outindent.class);
		arp.addMapping("outorderware", "oowId", Outorderware.class);
		arp.addMapping("product", "pId", Product.class);
		arp.addMapping("productformat", "pfId", Productformat.class);
		arp.addMapping("publicinfo", "piId", Publicinfo.class);
		arp.addMapping("question", "qId", Question.class);
		arp.addMapping("reportevaluatereasons", "rerId", Reportevaluatereasons.class);
		arp.addMapping("reportgoodreasons", "rgrId", Reportgoodreasons.class);
		arp.addMapping("reportmanufacturereasons", "rmrId", Reportmanufacturereasons.class);
		arp.addMapping("returncatalog", "rcId", Returncatalog.class);
		arp.addMapping("returngoodmould", "rgmId", Returngoodmould.class);
		arp.addMapping("returngoodreasons", "rgrId", Returngoodreasons.class);
		arp.addMapping("returngoods", "rgId", Returngoods.class);
		arp.addMapping("scorerank", "srId", Scorerank.class);
		arp.addMapping("sensitive", "senId", Sensitive.class);
		arp.addMapping("smsverification", "smsId", Smsverification.class);
		arp.addMapping("stock", "sId", Stock.class);
		arp.addMapping("store", "sId", Store.class);
		arp.addMapping("storebill", "sbId", Storebill.class);
		arp.addMapping("visitor", "vId", Visitor.class);
		arp.addMapping("ware", "wId", Ware.class);
		arp.addMapping("wareattribute", "waId", Wareattribute.class);
		arp.addMapping("warebelong", "wbId", Warebelong.class);
		arp.addMapping("warebonus", "wbId", Warebonus.class);
		arp.addMapping("warefdispatch", "wfdId", Warefdispatch.class);
		arp.addMapping("waresdispatch", "wsdId", Waresdispatch.class);
	}
}
