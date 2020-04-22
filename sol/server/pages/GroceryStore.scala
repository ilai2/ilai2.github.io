package guizilla.sol.server.pages

import guizilla.src.Page

/**
  * A class representing an online apple/banana/milk/egg/cookie vendor
  */
class GroceryStore extends Page {

  /**
    * An integer representing the number of apples the user wishes to buy
    */
  private var numApples: Int = 0

  /**
    * An integer representing the number of bananas the user wishes to buy
    */
  private var numBananas: Int = 0

  /**
    * An integer representing the number of milk cartons the user wishes to buy
    */
  private var numMilk: Int = 0

  /**
    * An integer representing the number of egg cartons the user wishes to buy
    */
  private var numEggs: Int = 0

  /**
    * An integer representing the number of cookies the user wishes to buy
    */
  private var numCookies: Int = 0

  /**
    * A double representing the total cost of the purchase
    */
  private var totalCost: Double = 0.0

  override def clone: Page = {
    val newPage = super.clone.asInstanceOf[GroceryStore]
    newPage.numApples = numApples
    newPage.numBananas = numBananas
    newPage.numMilk = numMilk
    newPage.numEggs = numEggs
    newPage.numCookies = numCookies
    newPage.totalCost = totalCost
    newPage
  }

  override def defaultHandler(inputs: Map[String, String], sessionId: String): String = {
    purchaseGroceries(inputs, sessionId)
  }

  /**
    * A page that asks for how many apples the user wishes to purchase, with a
    * form for input
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def purchaseGroceries(inputs: Map[String, String], sessionId: String): String = {
    "<html><body><p>Welcome to the Grocery Store!</p>" +
      "<form method=\"post\" action=\"/id:" + sessionId + "/purchaseBananas\">" +
      "<p> Please enter the number of apples you wish to purhcase. Each apple costs $0.99. </p>" +
      "<input type=\"text\" name=\"numApples\" />" +
      "<input type=\"submit\" value=\"submit\" />" +
      "</form></body></html>"
  }

  /**
    * A page that asks for how many bananas the user wishes to purchase, with a
    * form for input
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def purchaseBananas(inputs: Map[String, String], sessionId: String): String = {
    inputs.get("numApples") match {
      case Some(num) =>
        try {
          numApples = num.toInt
          if (numApples >= 0) {
            totalCost += 0.99 * numApples
            "<html><body><p>You currently have " + numApples + " apple(s) in your shopping cart." +
              "\nYour current total is $" + f"$totalCost%.2f" + ". </p>" +
              "<form method=\"post\" action=\"/id:" + sessionId + "/purchaseMilk\">" +
              "<p> Please enter the number of bananas you wish to purhcase. Each banana costs $1.50. </p>" +
              "<input type=\"text\" name=\"numBananas\" />" +
              "<input type=\"submit\" value=\"submit\" />" + "</form>" +
              "<p><a href=\"/id:" + sessionId + "/purchaseGroceries\">Reset your cart</a></p></body></html>"
          } else {
            "<html><body><p>I'm sorry you did not input a valid amount." +
              "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
          }
        } catch {
          case _: NumberFormatException =>
            "<html><body><p>I'm sorry you did not input a valid number." +
              "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
        }
      case None =>
        "<html><body><p>I'm sorry, there was an error retrieving your input." +
          "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
    }
  }

  /**
    * A page that asks for how much milk the user wishes to purchase, with a
    * form for input
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def purchaseMilk(inputs: Map[String, String], sessionId: String): String = {
    inputs.get("numBananas") match {
      case Some(num) =>
        try {
          numBananas = num.toInt
          if (numBananas >= 0) {
            totalCost += 1.50 * numBananas
            "<html><body><p>You currently have " + numApples + " apple(s) and " +
              numBananas + " banana(s) in your shopping cart. </p>" +
              "<p>Your current total is $" + f"$totalCost%.2f" + ". </p>" +
              "<form method=\"post\" action=\"/id:" + sessionId + "/purchaseEggs\">" +
              "<p> Please enter the number of cartons of milk you wish to purhcase. Each carton costs $5.00. </p>" +
              "<input type=\"text\" name=\"numMilk\" />" +
              "<input type=\"submit\" value=\"submit\" />" + "</form><p>" +
              "<a href=\"/id:" + sessionId + "/purchaseBananas\">Edit banana purchase</a></p></body></html>"
          } else {
            "<html><body><p>I'm sorry you did not input a valid amount." +
              "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
          }
        } catch {
          case _: NumberFormatException =>
            "<html><body><p>I'm sorry you did not input a valid number." +
              "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
        }
      case None =>
        "<html><body><p>I'm sorry, there was an error retrieving your input." +
          "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
    }
  }

  /**
    * A page that asks for how many eggs the user wishes to purchase, with a
    * form for input
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def purchaseEggs(inputs: Map[String, String], sessionId: String): String = {
    inputs.get("numMilk") match {
      case Some(num) =>
        try {
          numMilk = num.toInt
          if (numMilk >= 0) {
            totalCost += 5.00 * numMilk
            "<html><body><p>You currently have " + numApples + " apple(s), " +
              numBananas + " banana(s), and " + numMilk + " carton(s) of milk in your shopping cart. </p>" +
              "<p>Your current total is $" + f"$totalCost%.2f" + ". </p>" +
              "<form method=\"post\" action=\"/id:" + sessionId + "/purchaseCookies\">" +
              "<p> Please enter the number of cartons of eggs you wish to purhcase. Each carton costs $6.75. </p>" +
              "<input type=\"text\" name=\"numEggs\" />" +
              "<input type=\"submit\" value=\"submit\" />" + "</form><p>" +
              "<a href=\"/id:" + sessionId + "/purchaseMilk\">Edit milk purchase</a></p></body></html>"
          } else {
            "<html><body><p>I'm sorry you did not input a valid amount." +
              "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
          }
        } catch {
          case _: NumberFormatException =>
            "<html><body><p>I'm sorry you did not input a valid number." +
              "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
        }
      case None =>
        "<html><body><p>I'm sorry, there was an error retrieving your input." +
          "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
    }
  }

  /**
    * A page that asks for how many cookies the user wishes to purchase, with a
    * form for input
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def purchaseCookies(inputs: Map[String, String], sessionId: String): String = {
    inputs.get("numEggs") match {
      case Some(num) =>
        try {
          numEggs = num.toInt
          if (numEggs >= 0) {
            totalCost += 6.75 * numEggs
            "<html><body><p>You currently have " + numApples + " apple(s), " +
              numBananas + " banana(s), " + numMilk + " carton(s) of milk, and " + numEggs +
              " carton(s) of eggs in your shopping cart. </p>" +
              "<p>Your current total is $" + f"$totalCost%.2f" + ". </p>" +
              "<form method=\"post\" action=\"/id:" + sessionId + "/displayResult\">" +
              "<p> Please enter the number of cookies you wish to purhcase. Each cookies costs $2.50. </p>" +
              "<input type=\"text\" name=\"numCookies\" />" +
              "<input type=\"submit\" value=\"submit\" />" + "</form><p>" +
              "<a href=\"/id:" + sessionId + "/purchaseEggs\">Edit egg purchase</a></p></body></html>"
          } else {
            "<html><body><p>I'm sorry you did not input a valid amount." +
              "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
          }
        } catch {
          case _: NumberFormatException =>
            "<html><body><p>I'm sorry you did not input a valid number." +
              "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
        }
      case None =>
        "<html><body><p>I'm sorry, there was an error retrieving your input." +
          "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
    }
  }

  /**
    * A page that displays the total of the purchase and prompts the user to
    * confirm
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def displayResult(inputs: Map[String, String], sessionId: String): String = {
    inputs.get("numCookies") match {
      case Some(num) =>
        try {
          numCookies = num.toInt
          if (numCookies >= 0) {
            totalCost += 2.50 * numCookies
            "<html><body><p>You currently have " + numApples + " apple(s), " +
              numBananas + " banana(s), " + numMilk + " carton(s) of milk, " + numEggs +
              " carton(s) of eggs, and " + numCookies + " cookie(s) in your shopping cart. </p>" +
              "<p>Your current total is $" + f"$totalCost%.2f" + ". </p>" +
              "<form method=\"post\" action=\"/id:" + sessionId + "/totalPayment\">" +
              "<p> Please pay by entering the total amount below: </p>" +
              "<input type=\"text\" name=\"totalPayment\" />" +
              "<input type=\"submit\" value=\"submit\" />" + "</form><p>" +
              "<a href=\"/id:" + sessionId + "/purchaseCookies\">Edit cookie purchase</a></p></body></html>"
          } else {
            "<html><body><p>I'm sorry you did not input a valid amount." +
              "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
          }
        } catch {
          case _: NumberFormatException =>
            "<html><body><p>I'm sorry you did not input a valid number." +
              "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
        }
      case None =>
        "<html><body><p>I'm sorry, there was an error retrieving your input." +
          "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
    }
  }

  /**
    * A page that thanks the user for their purchase
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def totalPayment(inputs: Map[String, String], sessionId: String): String = {
    inputs.get("totalPayment") match {
      case Some(num) =>
        try {
          val totalPayment = num.toDouble
          if (totalCost == totalPayment) {
            "<html><body><p>Thank you for your purchase! Have a wonderful day!" +
              "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
          } else {
            "<html><body><p>I'm sorry, your payment amount is incorrect." +
              "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
          }
        } catch {
          case _: NumberFormatException =>
            "<html><body><p>I'm sorry, you did not input a valid number." +
              "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
        }
      case None =>
        "<html><body><p>I'm sorry, there was an error retrieving your input." +
          "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
    }
  }
}