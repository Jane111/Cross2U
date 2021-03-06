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
		arp.addMapping("administrator", "aId", Administrator.class);
		arp.addMapping("browserecord", "brId", Browserecord.class);
		arp.addMapping("business", "bId", Business.class);
		arp.addMapping("businesssearchrecord", "bsrId", Businesssearchrecord.class);
		arp.addMapping("collect", "cId", Collect.class);
		arp.addMapping("mainmanufacturer", "mmId", Mainmanufacturer.class);
		arp.addMapping("smsverification", "smsId", Smsverification.class);
		arp.addMapping("visitor", "vId", Visitor.class);
	}
}

