import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'category',
        data: { pageTitle: 'pimApp.category.home.title' },
        loadChildren: () => import('./category/category.module').then(m => m.CategoryModule),
      },
      {
        path: 'product',
        data: { pageTitle: 'pimApp.product.home.title' },
        loadChildren: () => import('./product/product.module').then(m => m.ProductModule),
      },
      {
        path: 'basket',
        data: { pageTitle: 'pimApp.basket.home.title' },
        loadChildren: () => import('./basket/basket.module').then(m => m.BasketModule),
      },
      {
        path: 'basket-item',
        data: { pageTitle: 'pimApp.basketItem.home.title' },
        loadChildren: () => import('./basket-item/basket-item.module').then(m => m.BasketItemModule),
      },
      {
        path: 'address',
        data: { pageTitle: 'pimApp.address.home.title' },
        loadChildren: () => import('./address/address.module').then(m => m.AddressModule),
      },
      {
        path: 'order',
        data: { pageTitle: 'pimApp.order.home.title' },
        loadChildren: () => import('./order/order.module').then(m => m.OrderModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
