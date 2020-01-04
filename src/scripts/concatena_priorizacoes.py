import sys


def main():
    post = sys.argv[1]
    with open('../dados/Priorizacao_{}.txt'.format(post), 'r') as tests:

        file_tests = tests.readlines()

        with open('../dados/tests-{}.txt'.format(post), 'r') as names:

            file_names = names.readlines()

            #Total
            with open('../dados/total_final-{}.txt'.format(post), 'w') as out:
                prio = file_tests[3].split(", ")

                for i in prio:
                    out.write(file_names[int(i)])

            #Additional
            with open('../dados/additinal_final-{}.txt'.format(post),
                      'w') as out:
                prio = file_tests[9].split(", ")

                for i in prio:
                    out.write(file_names[int(i)])

            #Echalon
            with open('../dados/echalon_final-{}.txt'.format(post),
                      'w') as out:
                prio = file_tests[15].split(", ")

                for i in prio:
                    out.write(file_names[int(i)])

            #Echalon Time
            with open('../dados/echalon_time_final-{}.txt'.format(post),
                      'w') as out:
                prio = file_tests[21].split(", ")

                for i in prio:
                    out.write(file_names[int(i)])

            #ARTMaxMin
            with open('../dados/art_max_min_final-{}.txt'.format(post),
                      'w') as out:
                prio = file_tests[27].split(", ")

                for i in prio:
                    out.write(file_names[int(i)])

            #Genetic
            with open('../dados/genetic_final-{}.txt'.format(post),
                      'w') as out:
                prio = file_tests[33].split(", ")

                for i in prio:
                    out.write(file_names[int(i)])


if __name__ == '__main__':
    main()
