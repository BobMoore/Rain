package Tests;

import com.follett.mywebapp.client.SetupValidation;
import com.follett.mywebapp.util.ValidationTreeDataItem;
import com.follett.mywebapp.util.ValidationTreeNode;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.Tree;

public class ServerMethodsTest extends GWTTestCase {


	 public void testSetupValidationClass() {
		 TestSetupValidation sv = new TestSetupValidation();
		 sv.testGetHighestTag();
	 }

	 class TestSetupValidation extends SetupValidation {

		public TestSetupValidation() {
			super(null, null);
		}

		public void testGetHighestTag() {
			Tree testTree = new Tree();
			ValidationTreeDataItem testItem = new ValidationTreeDataItem();
			assertEquals("Should return AAAAAA when there are no nodes in the tree.", getHighestTag(testTree), "AAAAAA");
			testItem.setTagID("AAAAAA");
			testTree.addItem(new ValidationTreeNode(testItem));
			assertEquals("Should return AAAAAB", getHighestTag(testTree), "AAAAAB");
			testItem.setTagID("AAAAAZ");
			testTree.addItem(new ValidationTreeNode(testItem));
			assertEquals("Should return AAAABA", getHighestTag(testTree), "AAAABA");
			testItem.setTagID("ZZZZZZ");
			testTree.addItem(new ValidationTreeNode(testItem));
			assertEquals("Should return AAAAAAA", getHighestTag(testTree), "AAAAAAA");
		}
	 }

	@Override
	public String getModuleName() {
		return null;
	}
}
