package com.cross2u.store.model;

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
		arp.addMapping("abnormalminfo", "amiId", Abnormalminfo.class);
		arp.addMapping("cooperation", "copId", Cooperation.class);
		arp.addMapping("manufacturer", "mId", Manufacturer.class);
		arp.addMapping("manukeyword", "mkId", Manukeyword.class);
		arp.addMapping("returngoodmould", "rgmId", Returngoodmould.class);
		arp.addMapping("scorerank", "srId", Scorerank.class);
		arp.addMapping("store", "sId", Store.class);
		arp.addMapping("storebill", "sbId", Storebill.class);
		arp.addMapping("warebelong", "wbId", Warebelong.class);
		arp.addMapping("warefdispatch", "wfdId", Warefdispatch.class);
		arp.addMapping("waresdispatch", "wsdId", Waresdispatch.class);
	}
}

