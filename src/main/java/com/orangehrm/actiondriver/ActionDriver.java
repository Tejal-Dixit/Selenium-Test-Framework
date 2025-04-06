package com.orangehrm.actiondriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class ActionDriver {

	private WebDriver driver;
	private WebDriverWait wait;
	public static final Logger logger = BaseClass.logger;

	public ActionDriver(WebDriver driver) {
		this.driver = driver;
		int explicitWait = Integer.parseInt(BaseClass.getProp().getProperty("explicitWait"));
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
		logger.info("WebDriver instance is created.");
	}

	// Method to click an element
	public void click(By by) {
		String elementDescription = getElementDescription(by);
		try {
			applyBorder(by,"green");
			waitForElementToBeClickable(by);
			driver.findElement(by).click();
			ExtentManager.logStep("clicked an element: "+elementDescription);
			logger.info("clicked an element-->" + elementDescription);
		} catch (Exception e) {
			applyBorder(by,"red");
			System.out.println("Unable to click element:" + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to click element:", elementDescription+"_unable to click");
			logger.error("unable to click element");
		}
	}

	// Method to enter text into an input field --Avoid Code Duplication - fix the
	// multiple calling method
	public void enterText(By by, String value) {
		try {
			waitForElementToBeVisible(by);
			applyBorder(by,"green");
			// driver.findElement(by).clear();
			// driver.findElement(by).sendKeys(value);
			WebElement element = driver.findElement(by);
			element.clear();
			element.sendKeys(value);
			logger.info("Entered text on " + getElementDescription(by) + "-->" + value);
		} catch (Exception e) {
			applyBorder(by,"red");
			logger.error("Unable to enter the value:" + e.getMessage());
		}
	}

	// Method to get text from an input field
	public String getText(By by) {
		try {
			waitForElementToBeVisible(by);
			applyBorder(by,"green");
			return driver.findElement(by).getText();
		} catch (Exception e) {
			applyBorder(by,"red");
			logger.error("Unable to get the text:" + e.getMessage());
			return "";
		}
	}

	// Method to compare Two text -- changed the return type
	public boolean compareText(By by, String expectedText) {
		try {
			waitForElementToBeVisible(by);
			String actualText = driver.findElement(by).getText();
			if (expectedText.equals(actualText)) {
				applyBorder(by,"green");
				logger.info("Texts are Matching:" + actualText + " equals " + expectedText);
				ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Compare Text", "Text Verified Successfully! "+actualText+ " equals "+expectedText);
				return true;
			} else {
				applyBorder(by,"red");
				logger.error("Texts are not Matching:" + actualText + " not equals " + expectedText);
				ExtentManager.logFailure(BaseClass.getDriver(), "Text Comparison Failed!", "Text Comparison Failed! "+actualText+ " not equals "+expectedText);
				return false;
			}
		} catch (Exception e) {
			applyBorder(by,"red");
			logger.error("Unable to compare Texts:" + e.getMessage());
		}
		return false;
	}

	/*
	 * Method to check if an element is displayed public boolean isDisplayed(By by)
	 * { try { waitForElementToBeVisible(by); boolean isDisplayed =
	 * driver.findElement(by).isDisplayed(); if (isDisplayed) {
	 * System.out.println("Element is Displayed"); return isDisplayed; } else {
	 * return isDisplayed; } } catch (Exception e) {
	 * System.out.println("Element is not displayed:"+e.getMessage()); return false;
	 * } }
	 */

	// Simplified the method and remove redundant conditions
	public boolean isDisplayed(By by) {
		try {
			waitForElementToBeVisible(by);
			applyBorder(by,"green");
			logger.info("Element is displayed " + getElementDescription(by));
			ExtentManager.logStep("Element is displayed: "+getElementDescription(by));
			ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Element is displayed: ", "Element is displayed: "+getElementDescription(by));
			return driver.findElement(by).isDisplayed();
		} catch (Exception e) {
			applyBorder(by,"red");
			logger.error("Element is not displayed: " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(),"Element is not displayed: ","Elemenet is not displayed: "+getElementDescription(by));
			return false;
		}
	}

	// Wait for the page to load
	public void waitForPageLoad(int timeOutInSec) {
		try {
			wait.withTimeout(Duration.ofSeconds(timeOutInSec)).until(WebDriver -> ((JavascriptExecutor) WebDriver)
					.executeScript("return document.readyState").equals("complete"));
			logger.info("Page loaded successfully.");
		} catch (Exception e) {
			logger.error("Page did not load within " + timeOutInSec + " seconds. Exception: " + e.getMessage());
		}
	}

	// Scroll to an element -- Added a semicolon ; at the end of the script string
	public void scrollToElement(By by) {
		try {
			applyBorder(by,"green");
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement element = driver.findElement(by);
			js.executeScript("arguments[0].scrollIntoView(true);", element);
		} catch (Exception e) {
			applyBorder(by,"red");
			logger.error("Unable to locate element:" + e.getMessage());
		}
	}

	// Wait for Element to be clickable
	private void waitForElementToBeClickable(By by) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			logger.error("element is not clickable: " + e.getMessage());
		}
	}

	// Wait for Element to be Visible
	private void waitForElementToBeVisible(By by) {
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			logger.error("Element is not visible:" + e.getMessage());
		}
	}

	// Method to get the description of an element using By locator
	public String getElementDescription(By locator) {
		// Check for null driver or locator to avoid NullPointerException
		if (driver == null) {
			return "Driver is not initialized.";
		}
		if (locator == null) {
			return "Locator is null.";
		}

		try {
			// Find the element using the locator
			WebElement element = driver.findElement(locator);

			// Get element attributes
			String name = element.getDomProperty("name");
			String id = element.getDomProperty("id");
			String text = element.getText();
			String className = element.getDomProperty("class");
			String placeholder = element.getDomProperty("placeholder");

			// Return a description based on available attributes
			if (isNotEmpty(name)) {
				return "Element with name: " + name;
			} else if (isNotEmpty(id)) {
				return "Element with ID: " + id;
			} else if (isNotEmpty(text)) {
				return "Element with text: " + truncate(text, 50);
			} else if (isNotEmpty(className)) {
				return "Element with class: " + className;
			} else if (isNotEmpty(placeholder)) {
				return "Element with placeholder: " + placeholder;
			} else {
				return "Element located using: " + locator.toString();
			}
		} catch (Exception e) {
			// Log exception for debugging
			e.printStackTrace(); // Replace with a logger in a real-world scenario
			return "Unable to describe element due to error: " + e.getMessage();
		}
	}

	// Utility method to check if a string is not null or empty
	private boolean isNotEmpty(String value) {
		return value != null && !value.isEmpty();
	}

	// Utility method to truncate long strings
	private String truncate(String value, int maxLength) {
		if (value == null || value.length() <= maxLength) {
			return value;
		}
		return value.substring(0, maxLength) + "...";
	}
	
	//Utility Method to Border an element
	public void applyBorder(By by,String color) {
		try {
			//Locate the element
			WebElement element = driver.findElement(by);
			//Apply the border
			String script = "arguments[0].style.border='3px solid "+color+"'";
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript(script, element);
			logger.info("Applied the border with color "+color+ " to element: "+getElementDescription(by));
		} catch (Exception e) {
			logger.warn("Failed to apply the border to an element: "+getElementDescription(by),e);
		}
	}
	
	 // ===================== Select Methods =====================
	
    // Method to select a dropdown by visible text
    public void selectByVisibleText(By by, String value) {
        try {
            WebElement element = driver.findElement(by);
            new Select(element).selectByVisibleText(value);
            applyBorder(by, "green");
            logger.info("Selected dropdown value: " + value);
        } catch (Exception e) {
            applyBorder(by, "red");
            logger.error("Unable to select dropdown value: " + value, e);
        }
    }
    
    // Method to select a dropdown by value
    public void selectByValue(By by, String value) {
        try {
            WebElement element = driver.findElement(by);
            new Select(element).selectByValue(value);
            applyBorder(by, "green");
            logger.info("Selected dropdown value by actual value: " + value);
        } catch (Exception e) {
            applyBorder(by, "red");
            logger.error("Unable to select dropdown by value: " + value, e);
        }
    }
    
    // Method to select a dropdown by index
    public void selectByIndex(By by, int index) {
        try {
            WebElement element = driver.findElement(by);
            new Select(element).selectByIndex(index);
            applyBorder(by, "green");
            logger.info("Selected dropdown value by index: " + index);
        } catch (Exception e) {
            applyBorder(by, "red");
            logger.error("Unable to select dropdown by index: " + index, e);
        }
    }
    
 // Method to get all options from a dropdown
    public List<String> getDropdownOptions(By by) {
        List<String> optionsList = new ArrayList<>();
        try {
            WebElement dropdownElement = driver.findElement(by);
            Select select = new Select(dropdownElement);
            for (WebElement option : select.getOptions()) {
                optionsList.add(option.getText());
            }
            applyBorder(by, "green");
            logger.info("Retrieved dropdown options for " + getElementDescription(by));
        } catch (Exception e) {
            applyBorder(by, "red");
            logger.error("Unable to get dropdown options: " + e.getMessage());
        }
        return optionsList;
    }
}
