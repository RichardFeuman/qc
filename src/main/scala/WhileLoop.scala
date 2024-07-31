object WhileLoop extends App {
  var x=0;

  while(x<10){
    println("val="+x)
    x+=1;
  }

  // difference between do .. while and while is
  // that do while execute at least once even if condition is false
  var counter=0;
  do {
    println("counter="+counter)
    counter+=1;
  } while(counter<10);

}
