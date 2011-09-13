
Object < #Example

+ yourself
   ^ self.
   
- testTrue
	^ true
	
- testFalse
	^ false
	
- testString
	^'Buh!'
	
- helloWorld: aName
	^aName