with open('dados/Priorizacao_do_exp1.txt', 'r') as tests:
    
    file_tests = tests.readlines()

    with open('dados/tests.txt', 'r') as names:
        
        file_names = names.readlines()
        
        #Total
        with open('dados/total_final.txt', 'w') as out:
            prio = file_tests[3].split(", ")

            for i in prio:
                out.write(file_names[int(i)])

        # Additional
        with open('dados/additinal_final.txt', 'w') as out:
            prio = file_tests[9].split(", ")

            for i in prio:
                out.write(file_names[int(i)])

        #Echalon
        with open('dados/echalon_final.txt', 'w') as out:
            prio = file_tests[15].split(", ")

            for i in prio:
                out.write(file_names[int(i)])
                
        #Echalon Time
        with open('dados/echalon_time_final.txt', 'w') as out:
            prio = file_tests[21].split(", ")
            
            for i in prio:
                out.write(file_names[int(i)])
