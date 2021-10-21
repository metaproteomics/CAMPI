import os
import pandas
import string


def mainPSM(myPath, result_file):

    """
    Returns peptidematrix of non-redundant peptides, check whether peptide is present (1) or not (0)
    I/L is replaced by J
    """
    def maxQuant(my_file):

        peptideList = list()
        with open(my_file, "r") as f:
            next(f)  # skip first line
            for line in f:
                peptide = line.split("\t")[0].upper().rstrip().replace("I", "J").replace("L", "J")
                peptideList.append(peptide)

        return peptideList

    def proteomeDiscoverer(my_file):

        peptideList = list()
        table = str.maketrans('', '', string.ascii_lowercase)
        with open(my_file, "r") as f:
            next(f)  # skip first line
            for line in f:
                peptide = line.split("\t")[4].split(".")[1].rstrip().replace("I", "J").replace("L", "J")
                peptide = peptide.translate(table)
                peptideList.append(peptide)

        return peptideList

    def galaxyP(my_file):

        peptideList = list()
        with open(my_file, "r") as f:
            next(f)  # skip first line
            for line in f:
                peptide = line.split("\t")[2].upper().rstrip().replace("I", "J").replace("L", "J")
                peptideList.append(peptide)

        return peptideList

    def MPA(my_file):

        peptideList = list()
        with open(my_file, "r") as f:
            next(f)  # skip first line
            for line in f:
                peptide = line.split("\t")[2].upper().rstrip().replace("I", "J").replace("L", "J")
                peptideList.append(peptide)

        return peptideList

    # Open a file
    sample_db = os.listdir(myPath)
    # dictionary for a db1-5
    completeResultsDict = dict()  # key = se; value = dict(key = dataset, value = peptidelist)

    # This would print all the files and directories
    for se in sample_db:
        if se not in completeResultsDict.keys():
            # sub-dictionary for a certain search pipeline
            searchEngineDict = dict()  # key = dataset, value = peptidelist)
            completeResultsDict[se] = searchEngineDict

        for result in os.listdir(myPath + "/" + se):
            peptideList = list()
            if se == "MQ":
                peptideList = maxQuant(myPath + "/" + se + "/" + result)
            elif se == "PD":
                peptideList = proteomeDiscoverer(myPath + "/" + se + "/" + result)
            elif se == "GP":
                if result.endswith(".tabular"):
                    peptideList = galaxyP(myPath + "/" + se + "/" + result)
            elif se == "MPA":
                peptideList = MPA(myPath + "/" + se + "/" + result)
            else:
                print("Are you sure?")

            # updating the completeResultsDict
            if peptideList:
                myDict = completeResultsDict.get(se)
                myDict[result.split(".", maxsplit=1)[0]] = peptideList

    # nested for-loop: {search engine: {dataset : peptidelist}}
    nonRedundantPeptideSet = set()
    count = 0
    for se, result in completeResultsDict.items():
        for dataset, peptides in result.items():
            for peptide in peptides:
                nonRedundantPeptideSet.add(peptide)
                count += 1
    nonRedundantPeptideList = sorted(list(nonRedundantPeptideSet))

    peptideMatrix = dict()
    peptideMatrix["PeptideSeq"] = nonRedundantPeptideList
    headerList = list()
    headerList.append("se_dataset")
    for se, result in completeResultsDict.items():
        print(se)
        for dataset, peptides in result.items():
            print(dataset)
            headerList.append("{}_{}".format(se, dataset))
            peptideList = []
            for peptide in nonRedundantPeptideList:
                if peptide in peptides:
                    peptideList.append(1)
                else:
                    peptideList.append(0)
            peptideMatrix["{}_{}".format(se, dataset)] = peptideList


    df = pandas.DataFrame(data=peptideMatrix)
    df.to_csv(open(result_file, "w", newline=''), index=False)


# SIHUMIx_REF
# myPath = <my_path>
# result_PSM = "peptidematrix_SIHUMIx_REF.csv"
# mainPSM(myPath=myPath, result_PSM=result_PSM)

# Gut_MO
# myPath = <my_path>
# result_PSM = "peptidematrix_Fecal_MO.csv"
# mainPSM(myPath=myPath, result_PSM=result_PSM)

