with open("intersection_lca_final_lineage.csv", 'r') as in_f, \
        open("intersection_lca_final_lineage_clean.csv", "w") as out_f:
    i = 0
    j = 0
    k = 0
    SIHUMIx_species = ["Anaerostipes caccae", "Bacteroides thetaiotaomicron", "Bifidobacterium longum",
                       "Blautia producta", "Clostridium butyricum", "Erysipelatoclostridium ramosum",
                       "Escherichia coli", "Lactobacillus plantarum"]  # Clostridium ramosum

    cRAP_species = ["Bos taurus", "Homo sapiens", "Ovis aries", "Lysobacter enzymogenes", "Sus scrofa",
                    "Staphylococcus aureus", "Saccharomyces cerevisiae virus L-A", "Equus caballus", "Saccharomyces cerevisiae",
                    "Oryctolagus cuniculus", "Gallus gallus", "Aequorea victoria", "Hevea brasiliensis", "Grifola frondosa"]
    for line in in_f:
        line = line.rstrip()
        j += 1
        # subfamily;tribe;subtribe;genus;subgenus;species group;species subgroup;species;subspecies;varietas;forma;EC;EC - names;GO (cellular component);GO (molecular function);GO (biological process);GO (cellular component) - names;GO (molecular function) - names;GO (biological process) - names;InterPro;InterPro - names
        pep, sk, ph, cl, order, fam, gen, sp = line.split(",")
        if sp in SIHUMIx_species:
            print(pep, sk, ph, cl, order, fam, gen, sp, sep=",", file=out_f)
            i += 1
        elif sp in cRAP_species:
            print(pep, "cRAP", "cRAP", "cRAP", "cRAP", "cRAP", "cRAP", "cRAP", sep=",", file=out_f)
            i += 1
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
            else:  # lca is found at species level
                if gen == "Blautia":  # all Blautia species were used in database, fair to merge them here
                    print(pep, sk, ph, cl, order, fam, gen, "Blautia producta", sep=",", file=out_f)
                print(pep, sk, ph, cl, order, fam, gen, sp, sep=",", file=out_f)

