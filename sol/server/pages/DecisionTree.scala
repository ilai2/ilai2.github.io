package guizilla.sol.server.pages

import guizilla.src.Page
import java.net.URLDecoder

class DecisionTree extends Page {
  private val html = "<html><body>"
  private val htmle = "</body></html>"
  private val beginning = """<h1>Decision Tree Generator</h1><br>
  <h2>We <spanred>make decisions</spanred> in every aspect of our lives; understanding why we make the decisions we do is crucial to making better ones and improving our lives. This web app helps you generate <spanred>decision tree diagrams</spanred> to visualize a decision in your life. You’ll need to know:<ol>
  <li>the <spanorange>choices available</spanorange> to you<br><teensy>I can eat ice cream or drink tea.</teensy></li>
  <li>the <spanyellow>potential outcomes</spanyellow> of those choices <br><teensy>I'm lactose intolerant, so I might get an upset stomach if I eat ice cream. Nothing will happen if I drink tea.</teensy></li>
  <li>the <spangreen>probability of those outcomes</spangreen> occurring <br>  <teensy>I think there's a 60% chance I have a negative reaction to the ice cream. There's a 100% chance I don't react negatively to tea.</teensy> </li>
  <li>the <spanblue>value or utility</spanblue> you place on each outcome <br><teensy> I really like ice cream though; I would give ice cream with no negative reaction 100 points. Even with the negative reaction, it's still 60 points. Tea is my favorite beverage, but it's not as good as ice cream: 70 points.</teensy></li>
</ol>
</h2>
</div>"""

  /**
    * A string representing the question the user asks
    */
  private var query: String = null

  /**
    * A string representing the user's name
    */
  private var name: String = null

  override def clone: Page = {
    val newPage = super.clone.asInstanceOf[MagicEightBall]
    newPage.query = this.query
    newPage.name = this.name
    newPage
  }
  override def defaultHandler(inputs: Map[String, String], sessionId: String): String =
    askForName(inputs, sessionId)



}

