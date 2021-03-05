with open("Gut_advMCH.csv", 'r') as in_f, open("Gut_advMCH_final.csv", "w") as out_f:
    next(in_f)
    i = 0
    j = 0
    k = 0

    cRAP_species = ["Bos taurus", "Homo sapiens", "Ovis aries", "Lysobacter enzymogenes", "Sus scrofa",
                    "Staphylococcus aureus", "Saccharomyces cerevisiae virus L-A", "Equus caballus", "Saccharomyces cerevisiae",
                    "Oryctolagus cuniculus", "Gallus gallus", "Aequorea victoria", "Hevea brasiliensis", "Grifola frondosa"]

    for line in in_f:
        j += 1
        # subfamily;tribe;subtribe;genus;subgenus;species group;species subgroup;species;subspecies;varietas;forma;EC;EC - names;GO (cellular component);GO (molecular function);GO (biological process);GO (cellular component) - names;GO (molecular function) - names;GO (biological process) - names;InterPro;InterPro - names
        pep, lca, sk, _, _, _, ph, _, _, cl, _, _, _, order, _, _, _, _, fam, _, _, _, gen, _, _, _, sp, _ = line.split(";", maxsplit=27)
        if lca == "root":
            print(pep, "root", "root", "root", "root", "root", "root", "root", sep=",", file=out_f)
        elif lca == "":
            print(pep, "unclassified", "unclassified", "unclassified", "unclassified", "unclassified", "unclassified", "unclassified", sep=",", file=out_f)
        elif sp in cRAP_species:
            print(pep, "cRAP", "cRAP", "cRAP", "cRAP", "cRAP", "cRAP", "cRAP", sep=",", file=out_f)
        else:
            if sk == "":
                print(pep, "root", "root", "root", "root", "root", "root", "root", sep=",", file=out_f)
            elif ph == "":
                print(pep, sk, "lca_superkingdom", "lca_superkingdom", "lca_superkingdom", "lca_superkingdom", "lca_superkingdom", "lca_superkingdom", sep=",", file=out_f)
            elif cl == "":
                print(pep, sk, ph, "lca_phylum", "lca_phylum", "lca_phylum", "lca_phylum", "lca_phylum", sep=",", file=out_f)
            elif order == "":
                print(pep, sk, ph, cl, "lca_class", "lca_class", "lca_class", "lca_class", sep=",", file=out_f)
            elif fam == "":
                print(pep, sk, ph, cl, order, "lca_order", "lca_order", "lca_order", sep=",", file=out_f)
            elif gen == "":
                print(pep, sk, ph, cl, order, fam, "lca_family", "lca_family", sep=",", file=out_f)
            elif sp == "":
                print(pep, sk, ph, cl, order, fam, gen, "lca_genus", sep=",", file=out_f)
            else:
                print(pep, sk, ph, cl, order, fam, gen, sp, sep=",", file=out_f)







            # if fam == "" and sp != "":
            #     print(line.rstrip())

        # i += 1
        # if i ==10:
        #     break
        #


print(i)
print(j)
print(i/j)
print(k)