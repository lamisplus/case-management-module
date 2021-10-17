package org.lamisplus.modules.casemanagement;

import com.foreach.across.AcrossApplicationRunner;
import com.foreach.across.config.AcrossApplication;

@AcrossApplication(
		modules = {
				
		}
)
public class CaseManagementApplication
{
	public static void main( String[] args ) {
		AcrossApplicationRunner.run( CaseManagementApplication.class, args );
	}
}
