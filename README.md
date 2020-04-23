# Isabel's JDM Emporium
Hello to the making decisions enthusiast viewing! Welcome to Isabel's JDM Emporium, where there's currently only one thing to do: generate decision tree diagrams!

Please generate to your heart's desire here: https://ilai2.github.io/decision-tree.html

Isabel is a student at Brown University and made this web app for CLPS0220: Making Decisions' final project. 

### If you're grading this...
Hi! Here was my approved proposal:

> My proposal for this task falls under the "programmer" category. I would like to create a website which would allow someone to create their own decision tree diagram based on their own inputted decision possibilities, what they believe the probabilities of success are, the outcomes, and utility they'd receive from each. I believe that this tool will help people think through their decisions more carefully and avoid decision making fallacies, such as the sunk cost fallacy.
>
>At each step of the way, there would be explanations of what that aspect of the tree means, and at the end, the "correct" branch would be recommended based on expected utility theory.
>
>I think this is a 200 point project as it requires a very in depth understanding of decision tree diagrams to program an application that can generate them — not only must I be able to explain on my website to the user what each aspect of it means, but I must understand all the possible combinations of a decision tree and any edge cases that may occur. Additionally, I must also understand expected utility theory in order to calculate it given arbitrary values.

Now we're here at the end of the project, I believe I completed my proposal pretty successfully. My generator allows people to make simple decision tree diagrams with 2 to 4 potential choices, and 1 to 2 outcomes from each of those. The reason why it's restricted is evident when you look at my code and see that, yes, I hard-coded everything (lists? never heard of them...Evan Dong, if you're reading this, don't roast me). 

It also contains a blurb at the beginning that explains what each component of the expected utility is. 

Problems with the project include:
1. The diagram doesn't refresh when you press the Make Diagram! button (everything else does refresh when you press the other buttons, and the decision you should make refreshes, jut not the diagram).
2. The code is the most messy, non-extensible thing I've ever written.
3. The diagram is a fixed size, so it doesn't scale well.

And others!
