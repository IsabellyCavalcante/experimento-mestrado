from xml.dom import minidom
import sys

def main(post):
    xml_doc = minidom.parse('../dados/execution_time-{}.xml'.format(post))
    test_list = xml_doc.getElementsByTagName('testcase')

    dict_test = {}

    for test in test_list:
        meth = test.attributes['name'].value.split(" ")[0]
        test_case = test.attributes['classname'].value
        
        tmp = u' '.join((test_case, meth))
        
        dict_test[tmp] = test.attributes['time'].value

    with open('../dados/tests-{}.txt'.format(post), 'rb') as tests, open('../dados/times-{}.txt'.format(post), 'w') as out:
        
        file_tests = tests.readlines()
        
        for name in file_tests:
            time = dict_test[name.decode('CP1252').rstrip()]
            out.write(time)
            out.write('\n')

    print ("fim")

if __name__ == "__main__":
    main(sys.argv[1])