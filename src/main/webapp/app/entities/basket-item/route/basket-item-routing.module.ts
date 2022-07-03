import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BasketItemComponent } from '../list/basket-item.component';
import { BasketItemDetailComponent } from '../detail/basket-item-detail.component';
import { BasketItemUpdateComponent } from '../update/basket-item-update.component';
import { BasketItemRoutingResolveService } from './basket-item-routing-resolve.service';

const basketItemRoute: Routes = [
  {
    path: '',
    component: BasketItemComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BasketItemDetailComponent,
    resolve: {
      basketItem: BasketItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BasketItemUpdateComponent,
    resolve: {
      basketItem: BasketItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BasketItemUpdateComponent,
    resolve: {
      basketItem: BasketItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(basketItemRoute)],
  exports: [RouterModule],
})
export class BasketItemRoutingModule {}
