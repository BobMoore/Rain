package Tests;

import junit.framework.TestCase;

import com.follett.mywebapp.util.SetupDataItem;


public class LocalMethodsTest extends TestCase {


//	 @Override
//	public String getModuleName() {
//		    return "com.follett.mywebapp.test";
//		  }


	 public void testSetupDataItemClass() {
		 TestSetupDataItem testItem = new TestSetupDataItem();
		 testItem.testIncrementTagID();
	 }

	 class TestSetupDataItem extends SetupDataItem {

		private static final long serialVersionUID = 1L;

		public void testIncrementTagID() {
			assertEquals("Should have 000001 in the return string.", "000001", incrementTagID(""));
			assertEquals("Should have 000010 in the return string.", "000010", incrementTagID("000009"));
			assertEquals("Should have seven digits in the return string.", "1000000", incrementTagID("999999"));
		}
	 }
}
