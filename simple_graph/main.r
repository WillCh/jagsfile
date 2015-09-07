# main for the gibbs sample
setwd('/home/datascience/Documents/jags/simple_graph')
######### define the data here ################
p.intelligence <- c(0.7, 0.3)
p.difficulty <- c(0.6,0.4)
p.sat <- structure( c(0.95,0.2, 0.05,0.8), .Dim = as.integer(c(2,2)) )
p.grade <- structure( c(.3,.9,.05,.5, .4,.08,.25,.3, .3,.02,.7,.2), .Dim = as.integer(c(2,2,3)) )
p.letter <- structure( c(0.1,0.4,0.99, 0.9,0.6,0.01), .Dim = as.integer(c(3,2)) )
alpha <- structure( c(100,100,1, 1,100,100), .Dim = as.integer(c(3,2)) )
# Now for some *real* data
N <- 10
intelligence <- c(1,1,1,1,1,1,1,1,1,1)
difficulty   <- c(1,1,1,1,1,1,1,1,1,1)
sat          <- c(1,1,1,1,1,1,1,1,1,1)
grade        <- c(1,1,1,2,2,2,3,3,3,3)
letter       <- c(NA,NA,2,NA,2,NA,NA,1,NA,NA)
############# end of the data define ##################
data_list = list ('p.intelligence' = p.intelligence,
             'p.difficulty' = p.difficulty, 
             'p.sat' = p.sat,
             'p.grade' = p.grade, 
             'p.letter' = p.letter,
             'alpha' = alpha,
             'N' = N,
             'intelligence' = intelligence,
             'difficulty' = difficulty,
             'sat' = sat,
             'grade' = grade,
             'letter' = letter)
library('rjags')

jags <- jags.model('student.bug',
                   data = data_list,
                   n.chains = 4,
                   n.adapt = 100)

update(jags, 1000)

N_sample = 500
sample_res<-coda.samples(jags,
                         c('grade', 'letter'),
                         N_sample)
##################### compute the cpt #######################

sample_matrix = as.matrix(sample_res)
cpt_letter = matrix(0, nrow = 3, ncol = 2)
for (row in 1:N_sample) {
  for (id in 1:N) {
    letter_id = "letter["
    letter_id = paste(letter_id, id, sep = "")
    letter_id = paste(letter_id, ']', sep="")
    grade_id = "grade["
    grade_id = paste(grade_id, id, sep = "")
    grade_id = paste(grade_id, ']', sep="")
    g = sample_matrix[row, grade_id]
    l = sample_matrix[row, letter_id]
    cpt_letter[g, l] = cpt_letter[g,l] + 1
  }
}
# normalize the cpt_letter
for (i in 1:3) {
  base = sum(cpt_letter[i, ])
  for (j in 1:2) {
    cpt_letter[i,j] = cpt_letter[i,j] *1.0 / base
  }
}
cpt_letter
summary(sample_res)
plot(sample_res)
