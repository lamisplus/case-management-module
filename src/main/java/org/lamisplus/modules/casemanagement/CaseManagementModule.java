package org.lamisplus.modules.casemanagement;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.web.AcrossWebModule;
import org.lamisplus.modules.base.BaseModule;
import org.lamisplus.modules.bootstrap.BootstrapModule;

@AcrossApplication(
		modules = {
				AcrossHibernateJpaModule.NAME,
				AcrossWebModule.NAME, BaseModule.NAME
		},
		modulePackageClasses = {BaseModule.class})
public class CaseManagementModule extends AcrossModule
{
	public final static String NAME = "CaseManagementModule";

	public CaseManagementModule(){
		super();
		addApplicationContextConfigurer(new ComponentScanConfigurer(
				getClass().getPackage().getName() +".controller",
				getClass().getPackage().getName() +".service",
				getClass().getPackage().getName() +".config",
				getClass().getPackage().getName() +".domain",
				getClass().getPackage().getName() +".domain.mapper",
				getClass().getPackage().getName() +".util",
				getClass().getPackage().getName() +".component"));
	}
	@Override
	public String getName() {
		return NAME;
	}
}
