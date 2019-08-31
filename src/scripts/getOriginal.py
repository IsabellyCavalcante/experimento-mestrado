from xml.dom import minidom
import sys

def main(post):
    xml_doc = minidom.parse('../dados/execution_time-{}.xml.format(post)')
    test_list = xml_doc.getElementsByTagName('testcase')

    with open('../dados/output/original-{}.txt'.format(post), 'w') as out:
        for test in test_list:
            meth = test.attributes['name'].value.split(" ")[0]
            test_case = test.attributes['classname'].value

            tmp = u' '.join((test_case, meth)).encode('utf-8')

            out.write(tmp)
            out.write('\n')

    print ("fim")


if __name__ == "__main__":
    main(sys.argv[1])