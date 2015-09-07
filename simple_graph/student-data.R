
# To use fixed values for certain variables, do, e.g.,
# "intelligence" <- 1 (w/out the "p", and check that the corresponding .bug file matches)
# Actually, even if we don't need these, we can leave these values here since the "p" distinguishes
# these from the normal variables, and we'll just get a warning saying that we aren't using these.
"p.intelligence" <- c(0.7, 0.3)
"p.difficulty" <- c(0.6,0.4)
"p.sat" <- structure( c(0.95,0.2, 0.05,0.8), .Dim = as.integer(c(2,2)) )
"p.grade" <- structure( c(.3,.9,.05,.5, .4,.08,.25,.3, .3,.02,.7,.2), .Dim = as.integer(c(2,2,3)) )
"p.letter" <- structure( c(0.1,0.4,0.99, 0.9,0.6,0.01), .Dim = as.integer(c(3,2)) )

"alpha" <- structure( c(100,100,1, 1,100,100), .Dim = as.integer(c(3,2)) )

"prior" <- c(100,100)

#"count" <- structure(
#c(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0), .Dim = as.integer(c(3,2,10)) )

# Now for some *real* data
"N" <- 10
"intelligence" <- c(1,1,1,1,1,1,1,1,1,1)
"difficulty"   <- c(1,1,1,1,1,1,1,1,1,1)
"sat"          <- c(1,1,1,1,1,1,1,1,1,1)
"grade"        <- c(1,1,1,2,2,2,3,3,3,3)
"letter"       <- c(NA,NA,NA,NA,NA,NA,NA,NA,NA,NA)
