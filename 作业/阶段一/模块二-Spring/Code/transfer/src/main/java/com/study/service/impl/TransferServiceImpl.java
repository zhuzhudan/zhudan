package com.study.service.impl;

import com.springframework.annotation.Autowired;
import com.springframework.annotation.Service;
import com.springframework.annotation.Transactional;
import com.study.dao.AccountDao;
import com.study.pojo.Account;
import com.study.service.TransferService;

@Service("transferService")
@Transactional
public class TransferServiceImpl implements TransferService {
    @Autowired
    private AccountDao accountDao;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {
            Account from = accountDao.queryAccountByCardNo(fromCardNo);
            Account to = accountDao.queryAccountByCardNo(toCardNo);

            from.setMoney(from.getMoney() - money);
            to.setMoney(to.getMoney() + money);

            accountDao.updateAccountByCardNo(to);
            int a =1/0;
            accountDao.updateAccountByCardNo(from);


    }
}
